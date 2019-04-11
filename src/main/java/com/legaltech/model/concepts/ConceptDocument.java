package com.legaltech.model.concepts;

import com.legaltech.model.Document;
import org.apache.solr.client.solrj.beans.Field;

public class ConceptDocument  implements Document {

    @Field
    private String id;
    @Field
    private String field;
    @Field
    private String  searchTerm;

    public ConceptDocument(String id, String field, String searchTerm) {
        this.id = id;
        this.field = field;
        this.searchTerm = searchTerm;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
}
