package com.metasflow.bff.domain.goal;

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

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public Goal createGoal(Goal goal) {
        try {
            String email = getCurrentUserEmail();
            log.info("Creating new goal: {} for user: {}", goal.getTitle(), email);
            
            goal.setEmail(email);
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
            String email = getCurrentUserEmail();
            log.info("Fetching all goals for user: {}", email);
            return repository.findByEmail(email);
        } catch (Exception e) {
            log.error("Error listing goals: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Goal getGoalById(String id) {
        String email = getCurrentUserEmail();
        return repository.findById(email, id)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + id + " for user: " + email));
    }

    public Goal updateGoal(String id, Goal goalDetails) {
        String email = getCurrentUserEmail();
        log.info("Updating goal: {} for user: {}", id, email);
        Goal goal = getGoalById(id);
        
        goal.setTitle(goalDetails.getTitle());
        goal.setDescription(goalDetails.getDescription());
        goal.setTargetValue(goalDetails.getTargetValue());
        goal.setLevel(goalDetails.getLevel());
        goal.setXp(goalDetails.getXp());
        goal.setStatus(goalDetails.getStatus());
        goal.setUpdatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        repository.save(goal);
        return goal;
    }

    public void deleteGoal(String id) {
        String email = getCurrentUserEmail();
        log.info("Deleting goal: {} for user: {}", id, email);
        repository.delete(email, id);
    }
}
