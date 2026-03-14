package com.metasflow.bff.domain.goal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbIgnore;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class Goal {

    private String pk; // PK
    private String sk; // SK
    private String type; // fixed "goal"
    private String title;
    private Integer progress;
    private String createdAt;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getSk() {
        return sk;
    }

    public void setSk(String sk) {
        this.sk = sk;
    }

    @DynamoDbAttribute("type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @DynamoDbAttribute("title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @DynamoDbAttribute("progress")
    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    @DynamoDbAttribute("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    private String category;
    private String deadline;
    private String description;

    @DynamoDbAttribute("category")
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @DynamoDbAttribute("deadline")
    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    @DynamoDbAttribute("description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private Integer frequency;
    private Boolean auto;
    private Double successRate;

    @DynamoDbAttribute("frequency")
    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    @DynamoDbAttribute("auto")
    public Boolean getAuto() {
        return auto;
    }

    public void setAuto(Boolean auto) {
        this.auto = auto;
    }

    @DynamoDbAttribute("success_rate")
    public Double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(Double successRate) {
        this.successRate = successRate;
    }

    private String goalId;
    private String subgoalId;
    private Integer xp;
    private Integer current;
    private Integer longest;
    private String lastActivity;

    @DynamoDbAttribute("goal_id")
    public String getGoalId() {
        return goalId;
    }

    public void setGoalId(String goalId) {
        this.goalId = goalId;
    }

    @DynamoDbAttribute("subgoal_id")
    public String getSubgoalId() {
        return subgoalId;
    }

    public void setSubgoalId(String subgoalId) {
        this.subgoalId = subgoalId;
    }

    @DynamoDbAttribute("xp")
    public Integer getXp() {
        return xp;
    }

    public void setXp(Integer xp) {
        this.xp = xp;
    }

    @DynamoDbAttribute("current")
    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    @DynamoDbAttribute("longest")
    public Integer getLongest() {
        return longest;
    }

    public void setLongest(Integer longest) {
        this.longest = longest;
    }

    @DynamoDbAttribute("last_activity")
    public String getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(String lastActivity) {
        this.lastActivity = lastActivity;
    }
}
