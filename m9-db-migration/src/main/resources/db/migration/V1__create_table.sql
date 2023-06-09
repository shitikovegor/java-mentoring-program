create table articles
(
    id        int8 not null generated by default as identity,
    text      varchar(255),
    title     varchar(255),
    author_id int8,
    primary key (id)
);

create table authors
(
    id   int8 not null generated by default as identity,
    name varchar(255),
    primary key (id)
);

alter table if exists articles
    add constraint authors_articles_fk
        foreign key (author_id) references authors;
