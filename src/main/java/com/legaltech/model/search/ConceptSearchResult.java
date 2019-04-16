package com.legaltech.model.search;

import java.util.List;

public class ConceptSearchResult {

    private final List<String> conceptsFilters;
    private final String unrecognizedQuery;

    public ConceptSearchResult(List<String> conceptsFilters, String unrecognizedQuery) {
        this.conceptsFilters = conceptsFilters;
        this.unrecognizedQuery = unrecognizedQuery;
    }

    public List<String> getConceptsFilters() {
        return conceptsFilters;
    }

    public String getUnrecognizedQuery() {
        return unrecognizedQuery;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConceptSearchResult that = (ConceptSearchResult) o;

        if (conceptsFilters != null ? !conceptsFilters.equals(that.conceptsFilters) : that.conceptsFilters != null)
            return false;
        return unrecognizedQuery != null ? unrecognizedQuery.equals(that.unrecognizedQuery) : that.unrecognizedQuery == null;
    }

    @Override
    public int hashCode() {
        int result = conceptsFilters != null ? conceptsFilters.hashCode() : 0;
        result = 31 * result + (unrecognizedQuery != null ? unrecognizedQuery.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ConceptSearchResult{" +
                "conceptsFilters=" + conceptsFilters +
                ", unrecognizedQuery='" + unrecognizedQuery + '\'' +
                '}';
    }
}
