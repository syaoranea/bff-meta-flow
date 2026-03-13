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
        String email = getCurrentUserEmail();
        log.info("Creating new goal: {} for user: {}", goal.getTitle(), email);
        
        goal.setEmail(email);
        if (goal.getSk() == null) {
            goal.setSk(UUID.randomUUID().toString());
        }
        
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        goal.setCreatedAt(now);
        goal.setUpdatedAt(now);
        
        if (goal.getStatus() == null) {
            goal.setStatus("active");
        }
        
        repository.save(goal);
        return goal;
    }

    public List<Goal> getAllGoals() {
        String email = getCurrentUserEmail();
        log.info("Fetching all goals for user: {}", email);
        return repository.findByEmail(email);
    }

    public Goal getGoalById(String sk) {
        String email = getCurrentUserEmail();
        return repository.findById(email, sk)
                .orElseThrow(() -> new RuntimeException("Goal not found with SK: " + sk + " for user: " + email));
    }

    public Goal patchGoal(String sk, Goal goalDetails) {
        String email = getCurrentUserEmail();
        log.info("Patching goal: {} for user: {}", sk, email);
        Goal goal = getGoalById(sk);
        
        if (goalDetails.getTitle() != null) goal.setTitle(goalDetails.getTitle());
        if (goalDetails.getDescription() != null) goal.setDescription(goalDetails.getDescription());
        if (goalDetails.getTargetValue() != null) goal.setTargetValue(goalDetails.getTargetValue());
        if (goalDetails.getLevel() != null) goal.setLevel(goalDetails.getLevel());
        if (goalDetails.getXp() != null) goal.setXp(goalDetails.getXp());
        if (goalDetails.getStatus() != null) goal.setStatus(goalDetails.getStatus());
        
        goal.setUpdatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        repository.save(goal);
        return goal;
    }

    public void deleteGoal(String sk) {
        String email = getCurrentUserEmail();
        log.info("Deleting goal: {} for user: {}", sk, email);
        repository.delete(email, sk);
    }
}
