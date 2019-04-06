package com.legaltech.service;

import com.legaltech.model.search.SearchQuery;
import org.apache.solr.client.solrj.SolrServerException;

import java.io.IOException;

/**
 * Search Service Interface.
 */
public interface SearchService {
    /**
     * @param query - query
     * @return Object that wraps json
     * @throws IOException - exception
     * @throws SolrServerException - exception
     */
    Object search(SearchQuery query) throws IOException, SolrServerException;
}
