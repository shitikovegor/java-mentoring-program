package com.jmp.migration.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.jmp.migration.model.Author;

@Repository
public interface AuthorRepository extends CrudRepository<Author, Long> {

    Optional<Author> findByName(final String name);

}
