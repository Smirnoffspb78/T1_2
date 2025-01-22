package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.config.property.TransactionProperty;
import ru.t1.java.demo.dto.response.TransactionDtoAccept;
import ru.t1.java.demo.dto.response.TransactionDtoResult;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static org.springframework.data.domain.Sort.Order.desc;
import static org.springframework.data.domain.Sort.by;
import static ru.t1.java.demo.model.enums.TransactionStatus.ACCEPTED;
import static ru.t1.java.demo.model.enums.TransactionStatus.BLOCKED;
import static ru.t1.java.demo.model.enums.TransactionStatus.REJECTED;


@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final ModelMapper modelMapper;

    private final TransactionProperty transactionProperty;

    @Override
    public TransactionDtoResult updateStatusAndSaveTransaction(TransactionDtoAccept transactionDtoAccept) {
        final TransactionDtoResult transactionDtoResult = modelMapper.map(transactionDtoAccept, TransactionDtoResult.class);
        final LocalDateTime beforeTime = transactionDtoAccept.getTransactionTime().minusMinutes(transactionProperty.intervalTime());
        final LocalDateTime afterTime = transactionDtoAccept.getTransactionTime().plusMinutes(transactionProperty.intervalTime());
        final Sort sort = by(desc(Transaction.Fields.transactionTime));
        final List<Transaction> transactions = transactionRepository.
                findAllByAccount_accountIdAndTransactionTimeAfterAndTransactionTimeBefore(transactionDtoAccept.getAccountId(), beforeTime, afterTime, sort);
        List<Transaction> maxTransactions = getMaxTransactions(transactions);
        if (maxTransactions.size() > transactionProperty.countTransaction()) {
            transactionDtoResult.setTransactionStatus(BLOCKED);
            final List<UUID> transactionsBlocked = transactions.stream()
                    .filter(transaction -> !transaction.getTransactionStatus().equals(BLOCKED))
                    .map(Transaction::getTransactionId)
                    .toList();
            transactionDtoResult.setTransactionsBlocked(transactionsBlocked);
        } else if (transactionDtoAccept.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            transactionDtoResult.setTransactionStatus(REJECTED);
            transactionDtoResult.setTransactionsBlocked(emptyList());
        } else {
            transactionDtoResult.setTransactionStatus(ACCEPTED);
            transactionDtoResult.setTransactionsBlocked(emptyList());
        }
        return transactionDtoResult;
    }

    private List<Transaction> getMaxTransactions(List<Transaction> transactions){
        if (transactions.isEmpty()) {
            return emptyList();
        }
        transactions.sort(Comparator.comparing(Transaction::getTransactionTime));
        Deque<Transaction> currentCountTransaction = new LinkedList<>();
        List<Transaction> maxTransactions = new ArrayList<>();
        int maxCount = 0;
        for (Transaction transaction : transactions) {
            LocalDateTime windowStart = transaction.getTransactionTime().minusMinutes(transactionProperty.intervalTime());
            while (!currentCountTransaction.isEmpty() && currentCountTransaction.getFirst().getTransactionTime().isBefore(windowStart)) {
                currentCountTransaction.pollFirst();
            }
            currentCountTransaction.addLast(transaction);
            if (currentCountTransaction.size() > maxCount) {
                maxCount = currentCountTransaction.size();
                maxTransactions = new ArrayList<>(currentCountTransaction);
            }
        }
        return maxTransactions;
    }
}
