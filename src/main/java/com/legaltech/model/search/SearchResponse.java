package com.legaltech.model.search;

import com.legaltech.model.Article;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.util.ArrayList;
import java.util.List;

public class SearchResponse {

    private long numFound;
    private final List<Article> articles = new ArrayList<>();
    private final List<String> matched = new ArrayList<>();

    public SearchResponse() { }
  
    public SearchResponse(QueryResponse queryResponse) {
        SolrDocumentList results = queryResponse.getResults();
        numFound = results.getNumFound();
        for (SolrDocument result : results) {
            String id = (String) result.getFieldValue("id");
            String title = (String) result.getFieldValue("title");
            articles.add(new Article(id, title));
        }
    }

    public SearchResponse(QueryResponse queryResponse, List<String> conceptsFilters) {
        SolrDocumentList results = queryResponse.getResults();
        numFound = results.getNumFound();
        for (SolrDocument result : results) {
            String id = (String) result.getFieldValue("id");
            String title = (String) result.getFieldValue("title");
            articles.add(new Article(id, title, queryResponse.getHighlighting().get(id)));
        }
        for (String filter : conceptsFilters) {
            String names = filter.split(":")[1];
            matched.add(names.substring(1, names.length() - 1));
        }
    }

    public long getNumFound() {
        return numFound;
    }

    public void setNumFound(final long numFound) {
        this.numFound = numFound;
    }

    public List<Article> getArticles() {
        return articles;
    }

}
