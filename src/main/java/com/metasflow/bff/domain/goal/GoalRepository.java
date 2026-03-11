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
        this.goalTable = enhancedClient.table("metasflow", TableSchema.fromBean(Goal.class));
    }

    public void save(Goal goal) {
        goalTable.putItem(goal);
    }

    public Optional<Goal> findById(String id) {
        return Optional.ofNullable(goalTable.getItem(Key.builder().partitionValue(id).build()));
    }

    public List<Goal> findAll() {
        return goalTable.scan().items().stream().collect(Collectors.toList());
    }

    public List<Goal> findByUserId(String userId) {
        // Simple scan for now, should ideally use a GSI for userId
        return goalTable.scan().items().stream()
                .filter(goal -> userId.equals(goal.getUserId()))
                .collect(Collectors.toList());
    }

    public void delete(String id) {
        goalTable.deleteItem(Key.builder().partitionValue(id).build());
    }
}
