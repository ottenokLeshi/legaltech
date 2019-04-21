package com.legaltech.service;


import com.legaltech.dao.ArticlesDAO;
import com.legaltech.dao.ConceptsDAO;
import com.legaltech.model.search.ConceptSearchResult;
import com.legaltech.model.search.SearchQuery;
import com.legaltech.model.search.SearchResponse;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
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

    @Value("${first.search.pass.dismax.mm}")
    private Integer firstSearchPassMaxOverlap;

    @Value("${second.search.pass.dismax.mm}")
    private Integer secondSearchPassMaxOverlap;

    @Value("${minimum.found.documents.amount}")
    private Integer minFoundAmount;

    @Override
    public SearchResponse search(SearchQuery searchQuery) throws IOException, SolrServerException {
        if (searchQuery == null) {
            searchQuery = new SearchQuery();
        }
        logger.info(searchQuery.getQuery());

        SearchResponse searchResponse = searchFirstPath(searchQuery);

        if (searchResponse.getNumFound() < minFoundAmount) {
            searchResponse = searchSecondPath(searchQuery);
        }
        return searchResponse;
    }

    private ConceptSearchResult getConcepts(SearchQuery searchQuery) throws IOException {
        return conceptsDAO.getConcepts(searchQuery.getQuery());
    }

    private SearchResponse searchFirstPath(SearchQuery searchQuery) throws IOException, SolrServerException {
        ConceptSearchResult conceptSearchResult = null;
        SolrQuery solrQuery;

        if (conceptSearchEnabled) {
            conceptSearchResult = getConcepts(searchQuery);
            solrQuery = buildFirstPathSolrQueryWithConcepts(searchQuery, conceptSearchResult);
            solrQuery.setQuery("{!dismax qf='text title' mm=" + firstSearchPassMaxOverlap + "%}" + conceptSearchResult.getUnrecognizedQuery());

        } else {
            solrQuery = buildFirstPathSolrQuery(searchQuery);
            solrQuery.setQuery("{!dismax qf='text title' mm=" + firstSearchPassMaxOverlap + "%}" + searchQuery.getQuery());

        }

        return getSearchResponse(solrQuery, conceptSearchResult);
    }

    private SearchResponse searchSecondPath(SearchQuery searchQuery) throws IOException, SolrServerException {
        ConceptSearchResult conceptSearchResult = null;
        SolrQuery solrQuery;

        if (conceptSearchEnabled) {
            conceptSearchResult = getConcepts(searchQuery);
            solrQuery = buildFirstPathSolrQueryWithConcepts(searchQuery, conceptSearchResult);
            solrQuery.setQuery("{!dismax qf='text title' mm=" + secondSearchPassMaxOverlap + "%}" + conceptSearchResult.getUnrecognizedQuery());

        } else {
            solrQuery = buildFirstPathSolrQuery(searchQuery);
            solrQuery.setQuery("{!dismax qf='text title' mm=" + secondSearchPassMaxOverlap + "%}" + searchQuery.getQuery());

        }

        return getSearchResponse(solrQuery, conceptSearchResult);
    }

    private SearchResponse getSearchResponse(SolrQuery solrQuery, ConceptSearchResult conceptSearchResult) throws IOException, SolrServerException {
        SearchResponse searchResponse;

        QueryResponse queryResponse = articleDAO.getArticles(solrQuery);

        if (conceptSearchEnabled) {
            searchResponse = new SearchResponse(queryResponse, conceptSearchResult.getConceptsFilters());
        } else {
            searchResponse = new SearchResponse(queryResponse);
        }

        return searchResponse;
    }

    private SolrQuery buildFirstPathSolrQueryWithConcepts(SearchQuery query, ConceptSearchResult conceptSearchResult) {
        SolrQuery solrQuery = buildFirstPathSolrQuery(query);

        for (String filter : conceptSearchResult.getConceptsFilters()) {
            solrQuery.addFilterQuery(filter);
        }

        return solrQuery;
    }

    private SolrQuery buildFirstPathSolrQuery(SearchQuery query) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setRows(query.getMaxResults());

        for (String filter : query.getFilter()) {
            solrQuery.addFilterQuery(filter);
        }

        addHighLight(solrQuery);

        return solrQuery;
    }

    private void addHighLight(SolrQuery solrQuery) {
        solrQuery.setHighlight(true);
        solrQuery.setHighlightFragsize(200);
        solrQuery.addHighlightField("title");
        solrQuery.addHighlightField("text");
    }
}
