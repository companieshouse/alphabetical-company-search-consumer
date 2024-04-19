package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.SameIntervalTopicReuseStrategy;
import org.springframework.messaging.Message;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.exception.RetryableException;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.util.MessageFlags;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.util.ServiceParameters;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;

/**
 * Consumes messages from the configured main Kafka topic.
 */
@Component
public class Consumer {

    private final UpsertService upsertService;
    private final MessageFlags messageFlags;

    public Consumer(UpsertService upsertService, MessageFlags messageFlags) {
        this.upsertService = upsertService;
        this.messageFlags = messageFlags;
    }

    /**
     * Consume a message from the main Kafka topic.
     *
     * @param message A message containing a payload.
     */
    @KafkaListener(
            id = "${consumer.group_id}",
            containerFactory = "kafkaListenerContainerFactory",
            topics = "${consumer.topic}",
            groupId = "${consumer.group_id}"
    )
    @RetryableTopic(
            attempts = "${consumer.max_attempts}",
            autoCreateTopics = "false",
            backoff = @Backoff(delayExpression = "${consumer.backoff_delay}"),
            retryTopicSuffix = "-${consumer.group_id}-retry",
            dltTopicSuffix = "-${consumer.group_id}-error",
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            sameIntervalTopicReuseStrategy = SameIntervalTopicReuseStrategy.SINGLE_TOPIC,
            include = RetryableException.class
    )
    public void consume(Message<ServiceParameters> message) throws ApiErrorResponseException, URIValidationException {
        try {
            ServiceParameters parameters = message.getPayload();
            // ResourceChangedData resourceChangedData = parameters.getData();
            System.out.println("message consuming");
            upsertService.upsertService(parameters);
            System.out.println("message consumed");
        } catch (RetryableException e) {
            // Handle RetryableException
            messageFlags.setRetryable(true);
            throw e; // Re-throw RetryableException after handling other exceptions
        }
    }
}
 