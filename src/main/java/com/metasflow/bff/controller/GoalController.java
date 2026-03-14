package com.metasflow.bff.controller;

import com.metasflow.bff.domain.goal.Goal;
import com.metasflow.bff.domain.goal.GoalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    public ResponseEntity<Goal> createGoal(@RequestBody Goal goal) {
        return ResponseEntity.ok(goalService.createGoal(goal));
    }

    @GetMapping
    public ResponseEntity<List<Goal>> getAllGoals() {
        return ResponseEntity.ok(goalService.getAllGoals());
    }

    @GetMapping("/{sk}")
    public ResponseEntity<Goal> getGoalById(@PathVariable String sk, @RequestParam(required = false) String pk) {
        return ResponseEntity.ok(goalService.getGoalById(pk, sk));
    }

    @GetMapping("/{sk}/subgoals")
    public ResponseEntity<List<Goal>> getSubgoals(@PathVariable String sk) {
        return ResponseEntity.ok(goalService.getSubgoals(sk));
    }

    @PatchMapping("/{sk}")
    public ResponseEntity<Goal> patchGoal(@PathVariable String sk, @RequestBody Goal goal) {
        return ResponseEntity.ok(goalService.patchGoal(sk, goal));
    }

    @DeleteMapping("/{sk}")
    public ResponseEntity<Void> deleteGoal(@PathVariable String sk, @RequestParam(required = false) String pk) {
        goalService.deleteGoal(sk, pk);
        return ResponseEntity.noContent().build();
    }
}
