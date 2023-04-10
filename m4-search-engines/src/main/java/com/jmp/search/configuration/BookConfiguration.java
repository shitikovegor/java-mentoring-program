package com.jmp.search.configuration;

import nl.siegmann.epublib.epub.EpubReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

@Configuration
@EnableSolrRepositories(basePackages = "com.jmp.search.repository", schemaCreationSupport = true)
public class BookConfiguration {

    @Bean
    public EpubReader epubReader() {
        return new EpubReader();
    }

}
