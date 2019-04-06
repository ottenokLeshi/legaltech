package com.legaltech.service;


import com.legaltech.dao.ArticlesDAO;
import com.legaltech.model.search.SearchQuery;
import com.legaltech.model.search.SearchResponse;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private ArticlesDAO articleDAO;
    @Autowired
    private Logger logger;

    @Override
    public SearchResponse search(SearchQuery query) throws IOException, SolrServerException {
        if (query == null) {
            query = new SearchQuery();
        }
        logger.info(query.getQuery());

        SearchResponse searchResponse = articleDAO.getArticles(query);

        return searchResponse;
    }
}
