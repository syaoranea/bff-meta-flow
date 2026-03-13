package com.metasflow.bff.domain.suggestion;

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
public class SuggestionService {

    private final SuggestionRepository repository;

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public Suggestion createSuggestion(Suggestion suggestion) {
        try {
            String email = getCurrentUserEmail();
            log.info("Creating new suggestion: {} for user: {}", suggestion.getTitle(), email);
            
            suggestion.setEmail(email);
            if (suggestion.getSk() == null) {
                suggestion.setSk(UUID.randomUUID().toString());
            }
            
            String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            suggestion.setCreatedAt(now);
            suggestion.setUpdatedAt(now);
            
            if (suggestion.getStatus() == null) {
                suggestion.setStatus("pending");
            }
            
            repository.save(suggestion);
            return suggestion;
        } catch (Exception e) {
            log.error("Error creating suggestion: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<Suggestion> getAllSuggestions() {
        try {
            String email = getCurrentUserEmail();
            log.info("Fetching all suggestions for user: {}", email);
            return repository.findByEmail(email);
        } catch (Exception e) {
            log.error("Error listing suggestions: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Suggestion getSuggestionById(String sk) {
        String email = getCurrentUserEmail();
        return repository.findById(email, sk)
                .orElseThrow(() -> new RuntimeException("Suggestion not found with SK: " + sk + " for user: " + email));
    }

    public Suggestion updateSuggestion(String sk, Suggestion suggestionDetails) {
        String email = getCurrentUserEmail();
        log.info("Updating suggestion: {} for user: {}", sk, email);
        Suggestion suggestion = getSuggestionById(sk);
        
        suggestion.setTitle(suggestionDetails.getTitle());
        suggestion.setDescription(suggestionDetails.getDescription());
        suggestion.setTargetValue(suggestionDetails.getTargetValue());
        suggestion.setLevel(suggestionDetails.getLevel());
        suggestion.setXp(suggestionDetails.getXp());
        suggestion.setStatus(suggestionDetails.getStatus());
        suggestion.setUpdatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        repository.save(suggestion);
        return suggestion;
    }

    public Suggestion patchSuggestion(String sk, Suggestion suggestionDetails) {
        String email = getCurrentUserEmail();
        log.info("Patching suggestion: {} for user: {}", sk, email);
        Suggestion suggestion = getSuggestionById(sk);
        
        if (suggestionDetails.getTitle() != null) suggestion.setTitle(suggestionDetails.getTitle());
        if (suggestionDetails.getDescription() != null) suggestion.setDescription(suggestionDetails.getDescription());
        if (suggestionDetails.getTargetValue() != null) suggestion.setTargetValue(suggestionDetails.getTargetValue());
        if (suggestionDetails.getLevel() != null) suggestion.setLevel(suggestionDetails.getLevel());
        if (suggestionDetails.getXp() != null) suggestion.setXp(suggestionDetails.getXp());
        if (suggestionDetails.getStatus() != null) suggestion.setStatus(suggestionDetails.getStatus());
        
        suggestion.setUpdatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        repository.save(suggestion);
        return suggestion;
    }

    public void deleteSuggestion(String sk) {
        String email = getCurrentUserEmail();
        log.info("Deleting suggestion: {} for user: {}", sk, email);
        repository.delete(email, sk);
    }
}
