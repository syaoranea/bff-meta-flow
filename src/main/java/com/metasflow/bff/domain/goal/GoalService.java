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
        String pk = goal.getPk();
        
        if (pk == null || pk.isEmpty() || pk.startsWith("USER#")) {
            pk = getPk();
            goal.setPk(pk);
            goal.setType("goal");
            if (goal.getSk() == null || !goal.getSk().startsWith("GOAL#")) {
                goal.setSk("GOAL#" + UUID.randomUUID().toString());
            }
        } else {
            // It's a subgoal
            goal.setType("subgoal");
            if (goal.getSk() == null || !goal.getSk().startsWith("SUBGOAL#")) {
                goal.setSk("SUBGOAL#" + UUID.randomUUID().toString());
            }
        }
        
        log.info("Creating {} : {} for PK: {}", goal.getType(), goal.getTitle(), pk);
        
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
        log.info("Fetching all top-level goals for user PK: {}", pk);
        return repository.findByPk(pk);
    }

    public List<Goal> getSubgoals(String parentSk) {
        log.info("Fetching subgoals for parent goal SK: {}", parentSk);
        String pk = parentSk.startsWith("GOAL#") ? parentSk : "GOAL#" + parentSk;
        return repository.findByPk(pk);
    }

    public Goal getGoalById(String pk, String sk) {
        if (pk == null || pk.isEmpty()) {
            pk = getPk();
        }
        // Ensure sk is formatted based on pk type
        String formattedSk = sk;
        if (pk.startsWith("USER#") && !sk.startsWith("GOAL#")) {
            formattedSk = "GOAL#" + sk;
        } else if (pk.startsWith("GOAL#") && !sk.startsWith("SUBGOAL#")) {
            formattedSk = "SUBGOAL#" + sk;
        }
        
        final String finalPk = pk;
        final String finalSk = formattedSk;
        
        return repository.findById(finalPk, finalSk)
                .orElseThrow(() -> new RuntimeException("Item not found with PK: " + finalPk + " and SK: " + finalSk));
    }

    public Goal patchGoal(String sk, Goal goalDetails) {
        Goal goal = getGoalById(goalDetails.getPk(), sk);
        log.info("Patching {}: {} for PK: {}", goal.getType(), goal.getSk(), goal.getPk());
        
        if (goalDetails.getTitle() != null) goal.setTitle(goalDetails.getTitle());
        if (goalDetails.getProgress() != null) goal.setProgress(goalDetails.getProgress());
        if (goalDetails.getCategory() != null) goal.setCategory(goalDetails.getCategory());
        if (goalDetails.getDeadline() != null) goal.setDeadline(goalDetails.getDeadline());
        if (goalDetails.getDescription() != null) goal.setDescription(goalDetails.getDescription());
        if (goalDetails.getFrequency() != null) goal.setFrequency(goalDetails.getFrequency());
        if (goalDetails.getAuto() != null) goal.setAuto(goalDetails.getAuto());
        if (goalDetails.getSuccessRate() != null) goal.setSuccessRate(goalDetails.getSuccessRate());
        
        repository.save(goal);
        return goal;
    }

    public void deleteGoal(String sk, String pk) {
        if (pk == null || pk.isEmpty()) {
            pk = getPk();
        }
        
        String formattedSk = sk;
        if (pk.startsWith("USER#") && !sk.startsWith("GOAL#")) {
            formattedSk = "GOAL#" + sk;
        } else if (pk.startsWith("GOAL#") && !sk.startsWith("SUBGOAL#")) {
            formattedSk = "SUBGOAL#" + sk;
        }
        
        log.info("Deleting item: {} for PK: {}", formattedSk, pk);
        repository.delete(pk, formattedSk);
    }
}
