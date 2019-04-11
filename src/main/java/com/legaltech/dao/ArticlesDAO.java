package com.legaltech.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.legaltech.model.Document;
import com.legaltech.model.search.ConceptSearchResult;
import com.legaltech.model.search.SearchQuery;
import com.legaltech.model.search.SearchResponse;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ArticlesDAO implements CoreDAO {
    /**
     * Solr DAO.
     */
    @Autowired private SolrDAO solrDAO;
    /**
     * Max mm (Minimum Should Match) Parameter.
     */
    private static final int MAXOVERLAP = 100;
    /**
     * Min mm (Minimum Should Match) Parameter.
     */
    private static final int MINOVERLAP = 100;

    /**
     * @param query - query
     * @return - Object that wraps json with search results
     * @throws IOException - exception
     * @throws SolrServerException - exception
     */
    public SearchResponse getArticles(final SearchQuery query)
            throws IOException, SolrServerException {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setRows(query.getMaxResults());

        for (String filter : query.getFilter()) {
            solrQuery.addFilterQuery(filter);
        }

        for (int mm = MAXOVERLAP; mm >= 0; mm -= MINOVERLAP) {
            solrQuery.setQuery("{!dismax qf='text title' mm=" + mm + "%}" + query.getQuery());

            QueryResponse queryResponse = solrDAO
                    .findForCustomQuery(ARTICLES, solrQuery);

            if (queryResponse.getResults().size() < query.getMinResults()) {
                continue;
            }

            return new SearchResponse(queryResponse);
        }

        return new SearchResponse();
    }

    /**
     * @param query - query
     * @param conceptSearchResult - instance with filters for solr query
     * @return - Object that wraps json with search results
     * @throws IOException - exception
     * @throws SolrServerException - exception
     */
    public SearchResponse getArticles(final SearchQuery query, final ConceptSearchResult conceptSearchResult)
            throws IOException, SolrServerException {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setRows(query.getMaxResults());

        for (String filter : conceptSearchResult.getConceptsFilters()) {
            solrQuery.addFilterQuery(filter);
        }

        for (String filter : query.getFilter()) {
            solrQuery.addFilterQuery(filter);
        }

        solrQuery.setHighlight(true);
        solrQuery.setHighlightFragsize(200);
        solrQuery.addHighlightField("title");
        solrQuery.addHighlightField("text");


        for (int mm = MAXOVERLAP; mm >= 0; mm -= MINOVERLAP) {
            if (conceptSearchResult.getUnrecognizedQuery()
                    .trim()
                    .length() > 0) {
                solrQuery.setQuery("{!dismax qf='text title' mm=" + mm + "%}"
                        + conceptSearchResult.getUnrecognizedQuery());
            } else {
                solrQuery.setQuery("*:*");
            }

            QueryResponse queryResponse = solrDAO
                    .findForCustomQuery(ARTICLES, solrQuery);

            if (queryResponse.getResults().size() < query.getMinResults()) {
                continue;
            }

            return new SearchResponse(queryResponse, conceptSearchResult.getConceptsFilters());
        }

        return new SearchResponse();
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
}
