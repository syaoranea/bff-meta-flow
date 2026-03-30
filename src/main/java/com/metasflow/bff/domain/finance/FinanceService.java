package com.metasflow.bff.domain.finance;

import com.metasflow.bff.domain.user.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinanceService {

    private final FinanceRepository repository;
    private final AuthService authService;

    private String getPk() {
        var user = authService.getCurrentUser();
        return "USER#" + user.getUserId();
    }

    public Transaction addTransaction(Transaction transaction) {
        String pk = getPk();
        transaction.setPk(pk);
        transaction.setSk("TRANS#" + UUID.randomUUID().toString());
        transaction.setType("transaction");
        
        if (transaction.getDate() == null) {
            transaction.setDate(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }

        log.info("Adding transaction: {} for user: {}", transaction.getDescription(), pk);
        repository.save(transaction);
        return transaction;
    }

    public List<Transaction> getAllTransactions() {
        String pk = getPk();
        log.info("Fetching all transactions for user: {}", pk);
        return repository.findByPk(pk).stream()
                .filter(t -> "transaction".equals(t.getType()))
                .toList();
    }

    public void deleteTransaction(String sk) {
        String pk = getPk();
        log.info("Deleting transaction: {} for user: {}", sk, pk);
        repository.delete(pk, sk);
    }
}
