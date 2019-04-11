package com.legaltech.service;


import com.legaltech.dao.ArticlesDAO;
import com.legaltech.dao.ConceptsDAO;
import com.legaltech.model.search.ConceptSearchResult;
import com.legaltech.model.search.SearchQuery;
import com.legaltech.model.search.SearchResponse;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private ArticlesDAO articleDAO;
    @Autowired
    private ConceptsDAO conceptsDAO;
    @Autowired
    private Logger logger;

    @Value("${concept.search.enabled}")
    private Boolean conceptSearchEnabled;


    @Override
    public SearchResponse search(SearchQuery query) throws IOException, SolrServerException {
        if (query == null) {
            query = new SearchQuery();
        }
        logger.info(query.getQuery());

        SearchResponse searchResponse;
        if (conceptSearchEnabled) {
            ConceptSearchResult conceptSearchResult = conceptsDAO.getConcepts(query.getQuery());
            searchResponse = articleDAO.getArticles(query, conceptSearchResult);
        } else {
            searchResponse = articleDAO.getArticles(query);
        }


        return searchResponse;
    }
}
