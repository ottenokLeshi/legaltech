package com.legaltech.service;


import com.legaltech.dao.ArticlesDAO;
import com.legaltech.dao.ConceptsDAO;
import com.legaltech.model.search.ConceptSearchResult;
import com.legaltech.model.search.SearchQuery;
import com.legaltech.model.search.SearchResponse;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.util.Pair;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

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
    public SearchResponse search(SearchQuery searchQuery) throws Exception {
        if (searchQuery == null) {
            searchQuery = new SearchQuery();
        }
        logger.info(searchQuery.getQuery());

        SearchResponse searchResponse = searchFirstPath(searchQuery);

//        if (searchResponse.getNumFound() < minFoundAmount) {
            searchResponse = searchSecondPath(searchQuery);
//        }
        return searchResponse;
    }

    private ConceptSearchResult getConcepts(SearchQuery searchQuery) throws IOException {
        return conceptsDAO.getConcepts(searchQuery.getQuery());
    }

    private SearchResponse searchFirstPath(SearchQuery searchQuery) throws Exception {
        Pair<SolrQuery, ConceptSearchResult> pair = createSolrQueryWithConcepts(searchQuery);

        SolrQuery solrQuery = pair.first();
        ConceptSearchResult conceptSearchResult = pair.second();

        return getSearchResponse(solrQuery, conceptSearchResult);
    }


    private Pair<SolrQuery, ConceptSearchResult> createSolrQueryWithConcepts(SearchQuery searchQuery) throws Exception {
        ConceptSearchResult conceptSearchResult = null;
        SolrQuery solrQuery;
        if (conceptSearchEnabled) {
            conceptSearchResult = getConcepts(searchQuery);
            solrQuery = buildFirstPathSolrQueryWithConcepts(searchQuery, conceptSearchResult);
            String unrecognized = "";
            if (conceptSearchResult.getUnrecognizedQuery().isEmpty() && !conceptSearchResult.getConceptsFilters().isEmpty()) {
                solrQuery.setQuery("*:*");
                return new Pair<>(solrQuery, conceptSearchResult);
            } else {
                unrecognized = conceptSearchResult.getUnrecognizedQuery();
            }
            solrQuery.setQuery("{!dismax qf='text title^2' mm=" + firstSearchPassMaxOverlap + "%}" + unrecognized);

        } else {
            solrQuery = buildFirstPathSolrQuery(searchQuery);
            solrQuery.setQuery("{!dismax qf='text title^2' mm=" + firstSearchPassMaxOverlap + "%}" + searchQuery.getQuery());
        }

        return new Pair<>(solrQuery, conceptSearchResult);
    }
    private SearchResponse searchSecondPath(SearchQuery searchQuery) throws Exception {
        SolrQuery spellSolrQuery = new SolrQuery();
        spellSolrQuery.setQuery(searchQuery.getQuery());
        spellSolrQuery.setParam("spellcheck", "on");
        String spellCheckResponse = articleDAO.getSpellCheckCorrection(spellSolrQuery);

//        String searchStringQuery = searchQuery.getQuery();
//        for (Map.Entry<String, String> entry : spellCheckResponse.entrySet()) {
//            searchStringQuery.replace(entry.getKey(), entry.getValue());
//        }
//
        searchQuery.setQuery(spellCheckResponse);
        Pair<SolrQuery, ConceptSearchResult> pair = createSolrQueryWithConcepts(searchQuery);

        SolrQuery solrQuery = pair.first();
        ConceptSearchResult conceptSearchResult = pair.second();

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
