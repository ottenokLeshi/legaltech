package com.legaltech.model;

import java.util.List;
import java.util.Map;

public class Article {
    public final String id;
    public final String title;
    public final String text;
    public Map<String, List<String>> highlighting;

    public Article(String id, String title, String text, Map<String, List<String>> highlighting) {
        this.id = id;
        this.text = text;
        this.title = title;
        this.highlighting = highlighting;
    }

    public Article(String id, String title, String text) {
        this.id = id;
        this.title = title;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Article article = (Article) o;

        if (id != null ? !id.equals(article.id) : article.id != null) return false;
        if (title != null ? !title.equals(article.title) : article.title != null) return false;
        if (text != null ? !text.equals(article.text) : article.text != null) return false;
        return highlighting != null ? highlighting.equals(article.highlighting) : article.highlighting == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (highlighting != null ? highlighting.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", highlighting=" + highlighting +
                '}';
    }
}
