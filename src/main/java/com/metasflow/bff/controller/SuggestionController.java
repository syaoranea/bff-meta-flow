package com.metasflow.bff.controller;

import com.metasflow.bff.domain.suggestion.Suggestion;
import com.metasflow.bff.domain.suggestion.SuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suggestions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SuggestionController {

    private final SuggestionService suggestionService;

    @PostMapping
    public ResponseEntity<Suggestion> createSuggestion(@RequestBody Suggestion suggestion) {
        return ResponseEntity.ok(suggestionService.createSuggestion(suggestion));
    }

    @GetMapping
    public ResponseEntity<List<Suggestion>> getAllSuggestions() {
        return ResponseEntity.ok(suggestionService.getAllSuggestions());
    }

    @GetMapping("/{sk}")
    public ResponseEntity<Suggestion> getSuggestionById(@PathVariable String sk) {
        return ResponseEntity.ok(suggestionService.getSuggestionById(sk));
    }

    @PutMapping("/{sk}")
    public ResponseEntity<Suggestion> updateSuggestion(@PathVariable String sk, @RequestBody Suggestion suggestion) {
        return ResponseEntity.ok(suggestionService.updateSuggestion(sk, suggestion));
    }

    @PatchMapping("/{sk}")
    public ResponseEntity<Suggestion> patchSuggestion(@PathVariable String sk, @RequestBody Suggestion suggestion) {
        return ResponseEntity.ok(suggestionService.patchSuggestion(sk, suggestion));
    }

    @DeleteMapping("/{sk}")
    public ResponseEntity<Void> deleteSuggestion(@PathVariable String sk) {
        suggestionService.deleteSuggestion(sk);
        return ResponseEntity.noContent().build();
    }
}
