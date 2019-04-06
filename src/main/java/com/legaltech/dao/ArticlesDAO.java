package com.legaltech.dao;

import com.legaltech.model.Document;
import com.legaltech.model.search.SearchQuery;
import com.legaltech.model.search.SearchResponse;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
public class ArticlesDAO {
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
     * Articles core name in Solr.
     */
    private static final String ARTICLES = "articles";

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
     * @throws IOException - exception
     * @throws SolrServerException - exception
     */
    public void commit() throws IOException, SolrServerException {
        solrDAO.commit(ARTICLES);
    }
}
