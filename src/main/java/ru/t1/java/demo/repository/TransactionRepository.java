package ru.t1.java.demo.repository;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.java.demo.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Override
    @NonNull
    Page<Transaction> findAll(@NonNull Pageable pageable);

    List<Transaction> findAllByAccount_accountIdAndTransactionTimeAfterAndTransactionTimeBefore(
            UUID accountId,
            LocalDateTime after,
            LocalDateTime before,
            Sort sort
    );
}
