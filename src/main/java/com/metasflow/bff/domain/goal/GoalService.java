package com.metasflow.bff.domain.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
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
        return repository.findByPk(pk).stream()
                .filter(g -> "goal".equals(g.getType()))
                .toList();
    }

    public List<Goal> getEvents() {
        String pk = getPk();
        log.info("Fetching all events for user PK: {}", pk);
        return repository.findByPk(pk).stream()
                .filter(g -> g.getType() != null && (g.getType().startsWith("EVENT_") || "subgoal_completed".equals(g.getType())))
                .toList();
    }

    public Goal getStreak() {
        String pk = getPk();
        log.info("Fetching streak for user PK: {}", pk);
        return repository.findById(pk, "STREAK").orElse(null);
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

    public void registerProgress(ProgressRequest request) {
        String userPk = getPk();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        // 1. Register the completion event
        Goal event = Goal.builder()
                .pk(userPk)
                .sk("EVENT#" + timestamp)
                .type("subgoal_completed")
                .goalId(request.getGoalId())
                .subgoalId(request.getSubgoalId())
                .xp(request.getXp())
                .createdAt(today)
                .build();
        repository.save(event);
        log.info("Progress registered for user {}: Subgoal {} of Goal {} completed", userPk, request.getSubgoalId(), request.getGoalId());

        // 2. Update Subgoal Success Rate
        updateSubgoalSuccessRate(request.getGoalId(), request.getSubgoalId(), today);

        // 3. Update Aggregate Events
        updateAggregateEvents(userPk, today);

        // 4. Update Streak
        updateStreak(userPk, today);
    }

    private void updateSubgoalSuccessRate(String goalId, String subgoalId, String today) {
        // goalId is the parent SK (PK of subgoal), subgoalId is the SK of subgoal
        Goal subgoal = repository.findById(goalId, subgoalId).orElse(null);
        if (subgoal == null) {
            log.warn("Subgoal not found: {} with parent {}", subgoalId, goalId);
            return;
        }

        if (subgoal.getCreatedAt() == null) {
            log.warn("Subgoal creation date not found: {}", subgoalId);
            return;
        }

        LocalDate creationDate = LocalDate.parse(subgoal.getCreatedAt());
        LocalDate todayDate = LocalDate.parse(today);
        long totalDaysSinceCreation = ChronoUnit.DAYS.between(creationDate, todayDate) + 1;

        if (subgoal.getCompletedDays() == null) subgoal.setCompletedDays(0);

        if (!today.equals(subgoal.getLastCompletedDate())) {
            subgoal.setCompletedDays(subgoal.getCompletedDays() + 1);
            subgoal.setLastCompletedDate(today);
        }

        double successRate = ((double) subgoal.getCompletedDays() / totalDaysSinceCreation) * 100.0;
        subgoal.setSuccessRate(Math.round(successRate * 10.0) / 10.0); // Round to 1 decimal place

        repository.save(subgoal);
        log.info("Subgoal {} success rate updated to {}% ({} days / {} total)", 
            subgoalId, subgoal.getSuccessRate(), subgoal.getCompletedDays(), totalDaysSinceCreation);
    }

    private void updateAggregateEvents(String userPk, String today) {
        LocalDate todayDate = LocalDate.parse(today);
        int year = todayDate.getYear();
        int month = todayDate.getMonthValue();
        int week = todayDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());

        String weekSk = String.format("EVENT#WEEK#%d#%02d", year, week);
        String monthSk = String.format("EVENT#MONTH#%d#%02d", year, month);
        String yearSk = String.format("EVENT#YEAR#%d", year);

        incrementAggregate(userPk, weekSk, "EVENT_WEEK");
        incrementAggregate(userPk, monthSk, "EVENT_MONTH");
        incrementAggregate(userPk, yearSk, "EVENT_YEAR");
    }

    private void incrementAggregate(String userPk, String sk, String type) {
        Goal aggregate = repository.findById(userPk, sk).orElse(null);
        if (aggregate == null) {
            aggregate = Goal.builder()
                    .pk(userPk)
                    .sk(sk)
                    .type(type)
                    .count(1)
                    .build();
        } else {
            aggregate.setCount((aggregate.getCount() == null ? 0 : aggregate.getCount()) + 1);
        }
        repository.save(aggregate);
    }

    private void updateStreak(String userPk, String today) {
        Goal streak = repository.findById(userPk, "STREAK").orElse(null);
        LocalDate todayDate = LocalDate.parse(today);

        if (streak == null) {
            log.info("Creating new streak for user {}", userPk);
            streak = Goal.builder()
                    .pk(userPk)
                    .sk("STREAK")
                    .type("STREAK")
                    .current(1)
                    .longest(1)
                    .lastActivity(today)
                    .build();
            repository.save(streak);
        } else {
            String lastActivityStr = streak.getLastActivity();
            if (lastActivityStr == null) {
                // Should not happen if created through here, but safety first
                streak.setCurrent(1);
                streak.setLastActivity(today);
                repository.save(streak);
                return;
            }

            LocalDate lastActivityDate = LocalDate.parse(lastActivityStr);

            if (lastActivityDate.equals(todayDate)) {
                log.info("Streak already updated today for user {}", userPk);
                return; // Already updated today
            }

            if (lastActivityDate.plusDays(1).equals(todayDate)) {
                // Consecutive day
                streak.setCurrent(streak.getCurrent() + 1);
                if (streak.getCurrent() > streak.getLongest()) {
                    streak.setLongest(streak.getCurrent());
                }
                log.info("Streak incremented to {} for user {}", streak.getCurrent(), userPk);
            } else {
                // Broke the streak
                streak.setCurrent(1);
                log.info("Streak reset for user {}", userPk);
            }

            streak.setLastActivity(today);
            repository.save(streak);
        }
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
