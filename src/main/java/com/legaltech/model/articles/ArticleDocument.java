package com.legaltech.model.articles;

import com.legaltech.model.Document;
import com.legaltech.model.posts.Post;
import org.apache.solr.client.solrj.beans.Field;

public class ArticleDocument implements Document {
    @Field
    private String supertype;
    @Field
    private String release_date;
    @Field
    private String doc_type;
    @Field
    private String text_source_url;
    @Field
    private String title;
    @Field
    private String doc_id;
    @Field
    private String text;

    public ArticleDocument(Post post) {
        this.supertype = post.supertype;
        this.release_date = post.release_date;
        this.doc_type = post.doc_type;
        this.text_source_url = post.text_source_url;
        this.title = post.title;
        this.doc_id = post.doc_id;
        this.text = post.text;
    }

    public String getSupertype() {
        return supertype;
    }

    public void setSupertype(String supertype) {
        this.supertype = supertype;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getDoc_type() {
        return doc_type;
    }

    public void setDoc_type(String doc_type) {
        this.doc_type = doc_type;
    }

    public String getText_source_url() {
        return text_source_url;
    }

    public void setText_source_url(String text_source_url) {
        this.text_source_url = text_source_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
