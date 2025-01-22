package ru.t1.java.demo.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @param countTransaction       Количество допустимых операций за диапазон времени intervalTime
 * @param intervalTime           Диапазон времени для подсчета допустимого количества сообщений, [мин]
 * @param transactionResultTopic Топик для отправки результата
 */
@ConfigurationProperties(prefix = "kafka.message")
public record TransactionProperty(
        long countTransaction,
        long intervalTime,
        String transactionResultTopic,
        String keyTransaction
) {
}
