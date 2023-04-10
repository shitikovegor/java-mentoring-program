package com.jmp.migration.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.jmp.migration.model.Article;

@Repository
public interface ArticleRepository extends CrudRepository<Article, Long> {

}
