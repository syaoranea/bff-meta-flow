package com.metasflow.bff.domain.goal;

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
public class GoalRepository {

    private final DynamoDbTable<Goal> goalTable;

    public GoalRepository(DynamoDbEnhancedClient enhancedClient) {
        // Table name is "metasflow-goals" as requested
        this.goalTable = enhancedClient.table("metasflow-goals", TableSchema.fromBean(Goal.class));
    }

    public void save(Goal goal) {
        goalTable.putItem(goal);
    }

    public Optional<Goal> findById(String email, String sk) {
        return Optional.ofNullable(goalTable.getItem(Key.builder()
                .partitionValue(email)
                .sortValue(sk)
                .build()));
    }

    public List<Goal> findByEmail(String email) {
        return goalTable.query(QueryConditional.keyEqualTo(Key.builder()
                .partitionValue(email)
                .build()))
                .items()
                .stream()
                .collect(Collectors.toList());
    }

    public void delete(String email, String sk) {
        goalTable.deleteItem(Key.builder()
                .partitionValue(email)
                .sortValue(sk)
                .build());
    }
}
