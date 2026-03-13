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
    private final com.metasflow.bff.domain.user.AuthService authService;

    private String getPk() {
        var user = authService.getCurrentUser();
        return "USER#" + user.getUserId();
    }

    public Goal createGoal(Goal goal) {
        String pk = getPk();
        log.info("Creating new goal: {} for user PK: {}", goal.getTitle(), pk);
        
        goal.setPk(pk);
        if (goal.getSk() == null || !goal.getSk().startsWith("GOAL#")) {
            goal.setSk("GOAL#" + UUID.randomUUID().toString());
        }
        
        goal.setType("goal");
        
        if (goal.getCreatedAt() == null) {
            goal.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        
        if (goal.getProgress() == null) {
            goal.setProgress(0);
        }
        
        repository.save(goal);
        return goal;
    }

    public List<Goal> getAllGoals() {
        String pk = getPk();
        log.info("Fetching all goals for user PK: {}", pk);
        return repository.findByPk(pk);
    }

    public Goal getGoalById(String sk) {
        String pk = getPk();
        // Ensure sk is formatted
        String formattedSk = sk.startsWith("GOAL#") ? sk : "GOAL#" + sk;
        return repository.findById(pk, formattedSk)
                .orElseThrow(() -> new RuntimeException("Goal not found with SK: " + formattedSk + " for user PK: " + pk));
    }

    public Goal patchGoal(String sk, Goal goalDetails) {
        Goal goal = getGoalById(sk);
        log.info("Patching goal: {} for user PK: {}", goal.getSk(), goal.getPk());
        
        if (goalDetails.getTitle() != null) goal.setTitle(goalDetails.getTitle());
        if (goalDetails.getProgress() != null) goal.setProgress(goalDetails.getProgress());
        
        repository.save(goal);
        return goal;
    }

    public void deleteGoal(String sk) {
        String pk = getPk();
        String formattedSk = sk.startsWith("GOAL#") ? sk : "GOAL#" + sk;
        log.info("Deleting goal: {} for user PK: {}", formattedSk, pk);
        repository.delete(pk, formattedSk);
    }
}
