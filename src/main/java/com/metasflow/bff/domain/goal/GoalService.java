package com.metasflow.bff.domain.goal;

import com.metasflow.bff.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalService {

    private final GoalRepository repository;

    public Goal createGoal(Goal goal) {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            log.info("Creating new goal: {} for user: {}", goal.getTitle(), currentUserEmail);
            
            goal.setUserId(currentUserEmail); // Use email as userId linked to partition key of users table
            goal.setId(UUID.randomUUID().toString());
            String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            goal.setCreatedAt(now);
            goal.setUpdatedAt(now);
            
            if (goal.getStatus() == null) {
                goal.setStatus("pending");
            }
            
            repository.save(goal);
            return goal;
        } catch (Exception e) {
            log.error("Error creating goal: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<Goal> getAllGoals() {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            log.info("Fetching all goals for user: {}", currentUserEmail);
            return repository.findByUserId(currentUserEmail);
        } catch (Exception e) {
            log.error("Error listing goals: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<Goal> getGoalsByUserId(String userId) {
        return repository.findByUserId(userId);
    }

    public Goal getGoalById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + id));
    }

    public Goal updateGoal(String id, Goal goalDetails) {
        log.info("Updating goal: {}", id);
        Goal goal = getGoalById(id);
        
        goal.setTitle(goalDetails.getTitle());
        goal.setLevel(goalDetails.getLevel());
        goal.setXp(goalDetails.getXp());
        goal.setStatus(goalDetails.getStatus());
        goal.setUpdatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        repository.save(goal);
        return goal;
    }

    public void deleteGoal(String id) {
        log.info("Deleting goal: {}", id);
        repository.delete(id);
    }
}
