package com.metasflow.bff.domain.finance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class FinanceController {

    private final FinanceService financeService;

    @PostMapping("/transactions")
    public ResponseEntity<Transaction> addTransaction(@RequestBody Transaction transaction) {
        return ResponseEntity.ok(financeService.addTransaction(transaction));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(financeService.getAllTransactions());
    }

    @DeleteMapping("/transactions/{sk}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable String sk) {
        financeService.deleteTransaction(sk);
        return ResponseEntity.noContent().build();
    }
}
