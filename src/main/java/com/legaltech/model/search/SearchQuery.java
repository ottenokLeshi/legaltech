package com.legaltech.model.search;

import java.util.ArrayList;
import java.util.List;

public class SearchQuery {

    private String query = "";
    private List<String> filter = new ArrayList<>();
    private int minResults = 1;
    private int maxResults = 10;

    public String getQuery() {
        return query;
    }

    public void setQuery(final String query) {
        this.query = query;
    }

    public List<String> getFilter() {
        return filter;
    }

    public void setFilter(final List<String> filter) {
        this.filter = filter;
    }

    public int getMinResults() {
        return minResults;
    }

    public void setMinResults(int minResults) {
        this.minResults = minResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public int getMaxResults() {
        return maxResults;
    }
}
