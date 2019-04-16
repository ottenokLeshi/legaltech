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
            String text = (String) result.getFieldValue("text");
            articles.add(new Article(id, title, text));
        }
    }

    public SearchResponse(QueryResponse queryResponse, List<String> conceptsFilters) {
        SolrDocumentList results = queryResponse.getResults();
        numFound = results.getNumFound();
        for (SolrDocument result : results) {
            String id = (String) result.getFieldValue("id");
            String title = (String) result.getFieldValue("title");
            String text = (String) result.getFieldValue("text");
            articles.add(new Article(id, title, text, queryResponse.getHighlighting().get(id)));
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchResponse that = (SearchResponse) o;

        if (numFound != that.numFound) return false;
        if (articles != null ? !articles.equals(that.articles) : that.articles != null) return false;
        return matched != null ? matched.equals(that.matched) : that.matched == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (numFound ^ (numFound >>> 32));
        result = 31 * result + (articles != null ? articles.hashCode() : 0);
        result = 31 * result + (matched != null ? matched.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SearchResponse{" +
                "numFound=" + numFound +
                ", articles=" + articles +
                ", matched=" + matched +
                '}';
    }
}
