package com.legaltech.dao;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.legaltech.model.Document;

import java.io.IOException;
import java.util.List;

@Repository
public class SolrDAO {
    /**
     * SolrClient instance.
     */
    @Autowired private SolrClient solrClient;

    /**
     * @param core - coreName
     * @param solrQuery - constructed Solr query
     * @return SolrDocumentList instance
     * @throws IOException - exception
     * @throws SolrServerException - exception
     */
    SolrDocumentList findDocuments(final String core, final SolrQuery solrQuery) throws IOException, SolrServerException {
        return solrClient.query(core, solrQuery).getResults();
    }

    /**
     * @param core - coreName
     * @param documents - document list to update
     * @throws IOException - exception
     * @throws SolrServerException - exception
     */
    void updateDocuments(final String core, final List<Document> documents) throws IOException, SolrServerException {
        solrClient.addBeans(core, documents);
    }

    /**
     * @param core - coreName
     * @throws IOException - exception
     * @throws SolrServerException - exception
     */
    void clearIndex(final String core) throws IOException, SolrServerException {
        solrClient.deleteByQuery(core, "*:*");
    }

    /**
     * @param core - coreName
     * @param solrQuery - constructed Solr query
     * @return query response
     * @throws IOException - exception
     * @throws SolrServerException - exception
     */
    QueryResponse findForCustomQuery(final String core, final SolrQuery solrQuery) throws IOException, SolrServerException {
        return solrClient.query(core, solrQuery);
    }

    /**
     * @param core - coreName
     * @throws IOException - exception
     * @throws SolrServerException - exception
     */
    void commit(final String core) throws IOException, SolrServerException {
        solrClient.commit(core);
    }
}
