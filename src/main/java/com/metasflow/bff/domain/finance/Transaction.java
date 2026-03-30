package com.metasflow.bff.domain.finance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class Transaction {
    private String pk; // USER#id
    private String sk; // TRANS#id
    private String type; // FIXED: transaction
    private Double amount;
    private String description;
    private String category;
    private String subcategory;
    private Boolean isCredit;
    private String transactionType; // expense or income
    private String date;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getPk() { return pk; }
    public void setPk(String pk) { this.pk = pk; }

    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getSk() { return sk; }
    public void setSk(String sk) { this.sk = sk; }

    @DynamoDbAttribute("type")
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    @DynamoDbAttribute("amount")
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    @DynamoDbAttribute("description")
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @DynamoDbAttribute("category")
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    @DynamoDbAttribute("subcategory")
    public String getSubcategory() { return subcategory; }
    public void setSubcategory(String subcategory) { this.subcategory = subcategory; }

    @DynamoDbAttribute("is_credit")
    public Boolean getIsCredit() { return isCredit; }
    public void setIsCredit(Boolean isCredit) { this.isCredit = isCredit; }

    @DynamoDbAttribute("transaction_type")
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    @DynamoDbAttribute("date")
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
