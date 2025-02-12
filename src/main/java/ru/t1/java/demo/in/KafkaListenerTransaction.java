package ru.t1.java.demo.in;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import ru.t1.java.demo.config.property.TransactionProperty;
import ru.t1.java.demo.dto.response.TransactionDtoAccept;
import ru.t1.java.demo.dto.response.TransactionDtoResult;
import ru.t1.java.demo.out.KafkaProducerService;
import ru.t1.java.demo.service.TransactionService;

@Component
@RequiredArgsConstructor
@Validated
public class KafkaListenerTransaction {

    private final TransactionService transactionService;
    private final TransactionProperty transactionProperty;

    private final KafkaProducerService kafkaProducerService;

    @KafkaListener(topics = "${kafka.message.transactionAcceptTopic}", groupId = "${kafka.config.consumer.transaction.groupIdTransaction}",
            containerFactory = "listenerFactoryTransaction")
    public void listenTransaction(@Valid @Payload TransactionDtoAccept transactionDtoaccept, Acknowledgment acknowledgment) {
        TransactionDtoResult transactionDtoAcceptResponse = transactionService.updateStatusAndSaveTransaction(transactionDtoaccept);
        acknowledgment.acknowledge();
        kafkaProducerService.sendMessage(transactionProperty.transactionResultTopic(), "contentType",
                transactionProperty.transactionResultTopic(), transactionDtoAcceptResponse, transactionProperty.keyTransaction());
    }
}
