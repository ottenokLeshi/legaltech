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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConceptDocument that = (ConceptDocument) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (field != null ? !field.equals(that.field) : that.field != null) return false;
        return searchTerm != null ? searchTerm.equals(that.searchTerm) : that.searchTerm == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (field != null ? field.hashCode() : 0);
        result = 31 * result + (searchTerm != null ? searchTerm.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ConceptDocument{" +
                "id='" + id + '\'' +
                ", field='" + field + '\'' +
                ", searchTerm='" + searchTerm + '\'' +
                '}';
    }
}
