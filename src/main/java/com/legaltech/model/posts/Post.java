package com.legaltech.model.posts;

public class Post {
    public String supertype;
    public String release_date;
    public String doc_type;
    public String text_source_url;
    public String title;
    public String doc_id;
    public String text;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Post post = (Post) o;

        if (supertype != null ? !supertype.equals(post.supertype) : post.supertype != null) return false;
        if (release_date != null ? !release_date.equals(post.release_date) : post.release_date != null) return false;
        if (doc_type != null ? !doc_type.equals(post.doc_type) : post.doc_type != null) return false;
        if (text_source_url != null ? !text_source_url.equals(post.text_source_url) : post.text_source_url != null)
            return false;
        if (title != null ? !title.equals(post.title) : post.title != null) return false;
        if (doc_id != null ? !doc_id.equals(post.doc_id) : post.doc_id != null) return false;
        return text != null ? text.equals(post.text) : post.text == null;
    }

    @Override
    public int hashCode() {
        int result = supertype != null ? supertype.hashCode() : 0;
        result = 31 * result + (release_date != null ? release_date.hashCode() : 0);
        result = 31 * result + (doc_type != null ? doc_type.hashCode() : 0);
        result = 31 * result + (text_source_url != null ? text_source_url.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (doc_id != null ? doc_id.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Post{" +
                "supertype='" + supertype + '\'' +
                ", release_date='" + release_date + '\'' +
                ", doc_type='" + doc_type + '\'' +
                ", text_source_url='" + text_source_url + '\'' +
                ", title='" + title + '\'' +
                ", doc_id='" + doc_id + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
