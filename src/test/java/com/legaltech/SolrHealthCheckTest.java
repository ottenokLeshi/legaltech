package com.legaltech;

import com.legaltech.config.SolrServerConfig;
import com.legaltech.config.SolrWebappRootSetter;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

/**
 * Useful link about used annotations:
 * https://docs.spring.io/spring-boot/docs/2.0.2.RELEASE/reference/htmlsingle/#boot-features-testing-spring-boot-applications
 */
@RunWith(SpringRunner.class)
@EnableAutoConfiguration
@SpringBootTest(classes = {SolrServerConfig.class, SolrWebappRootSetter.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "solr-config.home=./solr-config",
                "solr.ulog.dir=./ulog"
        }
)
public class SolrHealthCheckTest {

    @LocalServerPort
    int randomServerPort;

    @BeforeClass
    public static void initProperties() {
        // By some reason some of properties have to be initialized in this way
        System.setProperty("solr.data.dir", "./indexes");
    }

    /**
     * This is example shows how to query Solr
     */
    @Test
    public void solrHealthCheckTest() throws Exception {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("http://localhost:" + randomServerPort + "/articles/select?q=*:*");
        HttpResponse response = client.execute(request);
        assertEquals("We expect status code to be 200", 200, response.getStatusLine().getStatusCode());

        String responseBody = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
        System.out.println("Response body is: \n" + responseBody);
    }

}
