package com.legaltech.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.legaltech.model.Document;
import com.legaltech.model.search.ConceptSearchResult;
import com.legaltech.model.search.SearchQuery;
import com.legaltech.model.search.SearchResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.handler.component.SpellCheckComponent;
import org.apache.solr.spelling.SolrSpellChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ArticlesDAO implements CoreDAO {
    /**
     * Solr DAO.
     */
    @Autowired
    private SolrDAO solrDAO;

    @Value("${concept.search.enabled}")
    private Boolean conceptSearchEnabled;

    /**
     * @param solrQuery - query
     * @return - Object that wraps json with search results
     * @throws IOException - exception
     * @throws SolrServerException - exception
     */
    public QueryResponse getArticles(SolrQuery solrQuery)
            throws IOException, SolrServerException {
        return solrDAO.findForCustomQuery(ARTICLES, solrQuery);
    }

    /**
     * @param articles List of Articles
     * @throws IOException - exception
     * @throws SolrServerException - exception
     */
    public void setArticles(final List<Document> articles) throws IOException, SolrServerException {
        solrDAO.updateDocuments(ARTICLES, articles);
    }

    /**
     * @throws IOException - exception
     * @throws SolrServerException - exception
     */
    public void clearIndex() throws IOException, SolrServerException {
        solrDAO.clearIndex(ARTICLES);
    }

    /**
     * @param fields - List of fields in Articles
     * @return QueryResponse instance
     * @throws IOException - exception
     * @throws SolrServerException - exception
     */
    public QueryResponse getFacets(final List<String> fields) throws IOException, SolrServerException {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.add("json.facet", new JsonFacetConstructor().getFacetJsonString(fields));
        solrQuery.setQuery("*:*");

        return solrDAO.findForCustomQuery(ARTICLES, solrQuery);
    }

    /**
     * Class that generate json for facets.
     */
    private static class JsonFacetConstructor {
        /**
         * @param fields - List of fields in Articles
         * @return String that wraps json
         * @throws IOException - exception
         */
        String getFacetJsonString(final List<String> fields) throws IOException {

            Map<String, Map<String, Object>> facetsUnion = new HashMap<>();
            for (String field : fields) {
                TermsFacet termsFacet = new TermsFacet(field);
                facetsUnion.put(field, termsFacet.getFacetParameters());
            }

            return new ObjectMapper().writeValueAsString(facetsUnion);
        }

        /**
         *
         */
        static class TermsFacet {
            /**
             * Map with facet parameters.
             */
            private Map<String, Object> facetParameters = new LinkedHashMap<>();

            /**
             * @return map with facet parameters
             */
            Map<String, Object> getFacetParameters() {
                return facetParameters;
            }

            /**
             * @param field - field in Article
             */
            TermsFacet(final String field) {
                facetParameters.put("type", "terms");
                facetParameters.put("field", field);
                facetParameters.put("limit", Integer.MAX_VALUE);
            }
        }
    }

    /**
     * @throws IOException - exception
     * @throws SolrServerException - exception
     */
    public void commit() throws IOException, SolrServerException {
        solrDAO.commit(ARTICLES);
    }

    public String getSpellCheckCorrection(SolrQuery solrQuery) throws IOException, URISyntaxException {
        HttpClient client = HttpClientBuilder.create().build();
        URI uri = new URI("http://localhost:8080/articles/spell");
        uri = new URI(uri.getScheme(), uri.getAuthority(),
                uri.getPath(), "spellcheck=on&spellcheck.q=" + solrQuery.getQuery(), uri.getFragment());

        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);

        String fullEntity = EntityUtils.toString(response.getEntity());

        List rawConcepts = new ObjectMapper().readValue(fullEntity.substring(fullEntity.indexOf("\"suggestions\":[") + IDENTATION, fullEntity.length() - 1), List.class);

        return extractSpellCorrections(solrQuery.getQuery(), rawConcepts);
    }

    private  String extractSpellCorrections(final String searchString, final List rawConcepts) {
        Map<String, String> correctedTermsMap = new HashMap<>();
        String correctedString = searchString;
        for (int i = 0; i < rawConcepts.size(); i++) {
            if (i % 2 == 1) {
                continue;
            }
            String term = ((LinkedHashMap) ((ArrayList) ((LinkedHashMap) rawConcepts.get(i + 1)).get("suggestion")).get(0)).get("word").toString();
            String startOffset = ((LinkedHashMap) rawConcepts.get(i + 1)).get("startOffset").toString();
            String endOffset = ((LinkedHashMap) rawConcepts.get(i + 1)).get("endOffset").toString();
            correctedTermsMap.put(searchString.substring(Integer.parseInt(startOffset), Integer.parseInt(endOffset)), term);
        }

        for (Map.Entry<String, String> entry : correctedTermsMap.entrySet()) {
            correctedString = correctedString.replace(entry.getKey(), entry.getValue());
        }
        return correctedString;
    }

    /**
     * Identation constant in received json.
     */
    private static final int IDENTATION = 14;

    /**
     * Position of id in wraped list.
     */
    private static final int POSITION_ID = 7;


}
