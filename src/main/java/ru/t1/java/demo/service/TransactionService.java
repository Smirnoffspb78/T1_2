package ru.t1.java.demo.service;


import ru.t1.java.demo.dto.response.TransactionDtoAccept;
import ru.t1.java.demo.dto.response.TransactionDtoResult;

/**
 * Сервисный слой для работы с транзакциями.
 */
public interface TransactionService {

    public TransactionDtoResult updateStatusAndSaveTransaction(TransactionDtoAccept transactionDtoAcceptRequest);
}
