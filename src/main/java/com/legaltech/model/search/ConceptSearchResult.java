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
}
