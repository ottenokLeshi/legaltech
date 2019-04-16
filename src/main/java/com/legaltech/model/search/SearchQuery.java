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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchQuery that = (SearchQuery) o;

        if (minResults != that.minResults) return false;
        if (maxResults != that.maxResults) return false;
        if (query != null ? !query.equals(that.query) : that.query != null) return false;
        return filter != null ? filter.equals(that.filter) : that.filter == null;
    }

    @Override
    public int hashCode() {
        int result = query != null ? query.hashCode() : 0;
        result = 31 * result + (filter != null ? filter.hashCode() : 0);
        result = 31 * result + minResults;
        result = 31 * result + maxResults;
        return result;
    }

    @Override
    public String toString() {
        return "SearchQuery{" +
                "query='" + query + '\'' +
                ", filter=" + filter +
                ", minResults=" + minResults +
                ", maxResults=" + maxResults +
                '}';
    }
}
