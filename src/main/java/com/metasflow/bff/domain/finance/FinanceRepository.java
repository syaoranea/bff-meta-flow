package com.metasflow.bff.domain.finance;

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
public class FinanceRepository {

    private final DynamoDbTable<Transaction> transactionTable;

    public FinanceRepository(DynamoDbEnhancedClient enhancedClient) {
        this.transactionTable = enhancedClient.table("metasflow-goals", TableSchema.fromBean(Transaction.class));
    }

    public void save(Transaction transaction) {
        transactionTable.putItem(transaction);
    }

    public Optional<Transaction> findById(String pk, String sk) {
        return Optional.ofNullable(transactionTable.getItem(Key.builder()
                .partitionValue(pk)
                .sortValue(sk)
                .build()));
    }

    public List<Transaction> findByPk(String pk) {
        return transactionTable.query(QueryConditional.keyEqualTo(Key.builder()
                .partitionValue(pk)
                .build()))
                .items()
                .stream()
                .collect(Collectors.toList());
    }

    public void delete(String pk, String sk) {
        transactionTable.deleteItem(Key.builder()
                .partitionValue(pk)
                .sortValue(sk)
                .build());
    }
}
