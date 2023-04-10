package com.jmp.search.initialization;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.schema.SchemaDefinition;
import org.springframework.stereotype.Component;

import com.jmp.search.configuration.properties.BookProperties;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "books.suggest-field-loading-enabled", havingValue = "true")
public class SuggestFieldLoader {

    public static final String SUGGEST_FIELD_NAME = "suggest";

    private final SolrTemplate template;

    private final BookProperties properties;

    @EventListener
    public synchronized void createSuggestedField(final ContextRefreshedEvent event) {
        final var fieldsToAdd = new ArrayList<SchemaDefinition.SchemaField>();
        final var suggestField = new SchemaDefinition.FieldDefinition.Builder().named(SUGGEST_FIELD_NAME)
                .typedAs("text_general")
                .indexed()
                .stored()
                .muliValued()
                .create();
        fieldsToAdd.add(suggestField);
        properties.getSuggestFields().forEach(fieldName -> {
            final var copyField = new SchemaDefinition.CopyFieldDefinition();
            copyField.setSource(fieldName);
            copyField.setDestination(List.of(SUGGEST_FIELD_NAME));
            fieldsToAdd.add(copyField);
        });
        try {
            fieldsToAdd.forEach(field -> template.getSchemaOperations(properties.getCollection()).addField(field));
        } catch (final Exception e) {
            log.error("Error in time of adding fields", e);
        }
    }

}
