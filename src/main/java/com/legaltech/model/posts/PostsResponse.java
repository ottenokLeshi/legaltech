package com.legaltech.model.posts;

import java.util.List;

public class PostsResponse {

    public List<Post> posts;
    public Object meta;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PostsResponse that = (PostsResponse) o;

        if (posts != null ? !posts.equals(that.posts) : that.posts != null) return false;
        return meta != null ? meta.equals(that.meta) : that.meta == null;
    }

    @Override
    public int hashCode() {
        int result = posts != null ? posts.hashCode() : 0;
        result = 31 * result + (meta != null ? meta.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PostsResponse{" +
                "posts=" + posts +
                ", meta=" + meta +
                '}';
    }
}
