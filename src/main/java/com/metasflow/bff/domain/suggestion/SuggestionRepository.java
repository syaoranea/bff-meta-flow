package com.metasflow.bff.domain.suggestion;

import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class SuggestionRepository {

    private final DynamoDbTable<Suggestion> suggestionTable;

    public SuggestionRepository(DynamoDbEnhancedClient enhancedClient) {
        // Table name remains "metasflow" as requested
        this.suggestionTable = enhancedClient.table("metasflow", TableSchema.fromBean(Suggestion.class));
    }

    public void save(Suggestion suggestion) {
        suggestionTable.putItem(suggestion);
    }

    public Optional<Suggestion> findById(String email, String sk) {
        return Optional.ofNullable(suggestionTable.getItem(Key.builder()
                .partitionValue(email)
                .sortValue(sk)
                .build()));
    }

    public List<Suggestion> findAll() {
        return suggestionTable.scan().items().stream().collect(Collectors.toList());
    }

    public List<Suggestion> findByEmail(String email) {
        return suggestionTable.query(QueryConditional.keyEqualTo(Key.builder()
                .partitionValue(email)
                .build()))
                .items()
                .stream()
                .collect(Collectors.toList());
    }

    public void delete(String email, String sk) {
        suggestionTable.deleteItem(Key.builder()
                .partitionValue(email)
                .sortValue(sk)
                .build());
    }
}
