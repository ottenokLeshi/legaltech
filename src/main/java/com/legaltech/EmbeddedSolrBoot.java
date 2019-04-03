package com.legaltech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.legaltech")
@EnableAutoConfiguration(exclude = {SolrAutoConfiguration.class,
                                    SecurityAutoConfiguration.class})
public class EmbeddedSolrBoot {
    /**
     * @param args - system arguments
     */
    public static void main(final String[] args) {
        SpringApplication.run(EmbeddedSolrBoot.class, args);
    }
}
