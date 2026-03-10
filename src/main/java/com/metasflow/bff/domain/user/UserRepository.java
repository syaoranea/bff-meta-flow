package com.metasflow.bff.domain.user;

import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Optional;

@Repository
public class UserRepository {
    
    private final DynamoDbTable<User> userTable;

    public UserRepository(DynamoDbEnhancedClient enhancedClient) {
        this.userTable = enhancedClient.table("e3m3-users", TableSchema.fromBean(User.class));
    }

    public Optional<User> findByEmail(String email) {
        User user = userTable.getItem(Key.builder().partitionValue(email).build());
        return Optional.ofNullable(user);
    }

    public void save(User user) {
        userTable.putItem(user);
    }
}
