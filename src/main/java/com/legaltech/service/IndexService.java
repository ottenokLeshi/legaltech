package com.legaltech.service;

import org.apache.solr.client.solrj.SolrServerException;

import java.io.IOException;

public interface IndexService {
    void index() throws IOException, SolrServerException;
}
