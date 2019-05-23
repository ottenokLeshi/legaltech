package com.legaltech.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.legaltech.model.Document;
import com.legaltech.model.search.ConceptSearchResult;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Concepts DAO.
 */
@Repository
public class ConceptsDAO implements CoreDAO {

    /**
     * Solr DAO.
     */
    @Autowired
    private SolrDAO solrDAO;

    /**
     * Identation constant in received json.
     */
    private static final int IDENTATION = 7;

    /**
     * Position of id in wraped list.
     */
    private static final int POSITION_ID = 7;

    /**
     * Position of string tag that matched.
     */
    private static final int MACHED_TAG_ID = 5;

    /**
     * @param query - query
     * @return instance with filters for solr query
     * @throws IOException - exception
     */
    public ConceptSearchResult getConcepts(final String query) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost("http://localhost:8080/concepts/concept?field=searchTerm&matchText=true");
        request.setEntity(new StringEntity("{" + query + "}",  "UTF-8"));

        HttpResponse response = client.execute(request);

        String fullEntity = EntityUtils.toString(response.getEntity());
        List concepts = new ObjectMapper().readValue(fullEntity.substring(fullEntity.indexOf("\"docs\":[") + IDENTATION, fullEntity.length() - 1), List.class);

        List rawTags = new ObjectMapper().readValue(fullEntity.substring(fullEntity.indexOf("\"tags\":[") + IDENTATION, fullEntity.indexOf("\"response\":{") - 1), List.class);
        String unrecognizedQuery = excludeConcepts(rawTags, query);
        List<String> conceptsFilters = getConceptsFilters(rawTags, concepts);

        ConceptSearchResult conceptSearchResult = new ConceptSearchResult(conceptsFilters, unrecognizedQuery);

        return conceptSearchResult;
    }

    /**
     * @param concepts - List of concepts
     * @throws IOException - exception
     * @throws SolrServerException - exception
     */
    public void setConcepts(final List<Document> concepts) throws IOException, SolrServerException {
        solrDAO.updateDocuments(CONCEPTS, concepts);
    }

    /**
     * @throws IOException - exception
     * @throws SolrServerException - exception
     */
    public void clearIndex() throws IOException, SolrServerException {
        solrDAO.clearIndex(CONCEPTS);
    }
    /**
     * @throws IOException - exception
     * @throws SolrServerException - exception
     */
    public void commit() throws IOException, SolrServerException {
        solrDAO.commit(CONCEPTS);
    }

    /**
     * @param rawTags - list of concepts
     * @param concepts - wrapped concepts
     * @return - list with filters strings
     */
    private static List<String> getConceptsFilters(final List<List> rawTags, final List<LinkedHashMap> concepts) {
        List<String> filters = new ArrayList<>();

        for (List tag : rawTags) {
            String conceptId = ((List<String>) tag.get(POSITION_ID)).get(0);
            for (LinkedHashMap concept : concepts) {
                if (conceptId.equals(concept.get("id"))) {
                    filters.add(concept.get("field") + ":(" + concept.get("searchTerm") + ")");
                }
            }
        }

        return filters;
    }

    /**
     * @param rawTags -  list of concepts
     * @param query - original user query
     * @return String - unrecognizedQuery
     */
    private static String excludeConcepts(final List<List> rawTags, final String query) {
        List<String> matchedTags = new ArrayList<>();
        String newQuery = query;

        for (List tag : rawTags) {
            matchedTags.add((String) tag.get(MACHED_TAG_ID));
        }

        for (String matchedTag : matchedTags) {
            newQuery = newQuery.replace(matchedTag, "");
        }

        return newQuery;
    }
}
