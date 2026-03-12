package com.metasflow.bff.controller;

import com.metasflow.bff.domain.goal.Goal;
import com.metasflow.bff.domain.goal.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
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
    public ResponseEntity<Goal> getGoalById(@PathVariable String sk) {
        return ResponseEntity.ok(goalService.getGoalById(sk));
    }

    @PutMapping("/{sk}")
    public ResponseEntity<Goal> updateGoal(@PathVariable String sk, @RequestBody Goal goal) {
        return ResponseEntity.ok(goalService.updateGoal(sk, goal));
    }

    @PatchMapping("/{sk}")
    public ResponseEntity<Goal> patchGoal(@PathVariable String sk, @RequestBody Goal goal) {
        return ResponseEntity.ok(goalService.patchGoal(sk, goal));
    }

    @DeleteMapping("/{sk}")
    public ResponseEntity<Void> deleteGoal(@PathVariable String sk) {
        goalService.deleteGoal(sk);
        return ResponseEntity.noContent().build();
    }
}
