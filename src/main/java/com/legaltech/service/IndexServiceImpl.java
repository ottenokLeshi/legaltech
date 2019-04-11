package com.legaltech.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.legaltech.dao.ArticlesDAO;
import com.legaltech.dao.ConceptsDAO;
import com.legaltech.model.Document;
import com.legaltech.model.articles.ArticleDocument;
import com.legaltech.model.concepts.ConceptDocument;
import com.legaltech.model.posts.Post;
import com.legaltech.model.posts.PostsResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class IndexServiceImpl implements IndexService {

    /**
     * Articles api url.
     */
    @Value("${articles.api.url}")
    private String articlesUrl;

    @Value("${articles.filepath}")
    private String articlesPath;

    /**
     * Articles DAO.
     */
    @Autowired
    private ArticlesDAO articlesDAO;

    /**
     * Concepts DAO.
     */
    @Autowired
    private ConceptsDAO conceptsDAO;


    @Override
    public void index() throws IOException, SolrServerException {
        indexArticles();
        indexConcepts();
    }

    /**
     * @throws IOException - exception
     * @throws SolrServerException - exception
     */
    private void indexConcepts() throws IOException, SolrServerException {
        conceptsDAO.clearIndex();
        List<Document> concepts = new ConceptsGenerator().getConcepts(Arrays.asList("authors", "topic"));

        conceptsDAO.setConcepts(concepts);
        conceptsDAO.commit();
    }

    private void indexArticles() throws IOException, SolrServerException {
        articlesDAO.clearIndex();
        List<Document> articles = new ArrayList<>();

        for (Post post : new ArticlesGrabber().getPosts()) {
            StringBuilder builder = new StringBuilder();
            String fileName = post.doc_id.replace("/", "_") + ".txt";
//            System.out.println(articlesPath + "/" +fileName);
            try (BufferedReader br =
                         new BufferedReader(new FileReader(articlesPath + "/" + fileName))) {
                builder.append(br.readLine());
            }
            post.text = builder.toString();
            articles.add(new ArticleDocument(post));
        }

        articlesDAO.setArticles(articles);
        articlesDAO.commit();
    }

    /**
     * ConceptsGenerator class.
     */
    private class ConceptsGenerator {
        /**
         * @param fields - list of fields in Articles core
         * @return list with documents
         * @throws IOException - exception
         * @throws SolrServerException - exception
         */
        List<Document> getConcepts(final List<String> fields) throws IOException, SolrServerException {

            QueryResponse response = articlesDAO.getFacets(fields);

            List<Document> concepts = new ArrayList<>();
            int id = 0;

            for (String field : fields) {
                List<SimpleOrderedMap> recursive = (List<SimpleOrderedMap>) response
                        .getResponse()
                        .findRecursive("facets", field, "buckets");
                if (recursive == null) {
                    continue;
                }
                for (SimpleOrderedMap map : recursive) {
                    String term = map.get("val").toString();
                    concepts.add(new ConceptDocument(Integer.toString(id), field, term));
                    id++;
                }
            }

            return concepts;
        }
    }

    /**
     * PostsGrabber class.
     */
    private class ArticlesGrabber {

        /**
         * @return - list with posts
         * @throws IOException - exception
         */
        public List<Post> getPosts() throws IOException {
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet requestPosts = new HttpGet(articlesUrl);
            HttpResponse response = httpClient.execute(requestPosts);

            PostsResponse postsResponse = new ObjectMapper().readValue(EntityUtils.toString(response.getEntity()), PostsResponse.class);
//
//            BufferedReader br = new BufferedReader(new FileReader(file));
//
//            String st;
//            while ((st = br.readLine()) != null)
//                System.out.println(st);
//
//            PostsResponse postsResponse = new PostsResponse();
//            postsResponse.posts = responsePosts;
            return postsResponse.posts;
        }

    }
}
