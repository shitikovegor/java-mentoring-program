package com.jmp.search.repository;

import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

import com.jmp.search.model.Book;

@Repository
public interface BookRepository extends SolrCrudRepository<Book, String> {

    Book findByTitle(final String title);

}
