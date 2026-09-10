package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.utils;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

public final class TestUtils {

    public static final String MAIN_TOPIC = "echo";
    public static final String MAIN_GROUP = "echo-consumer";

    public static final String RETRY_TOPIC = "%s-%s-retry".formatted(MAIN_TOPIC, MAIN_GROUP);
    public static final String ERROR_TOPIC = "%s-%s-error".formatted(MAIN_TOPIC, MAIN_GROUP);
    public static final String INVALID_TOPIC = "%s-%s-invalid".formatted(MAIN_TOPIC, MAIN_GROUP);

    private TestUtils(){
    }

    public static int noOfRecordsForTopic(ConsumerRecords<?, ?> records, String topic) {
        int count = 0;
        for (ConsumerRecord<?, ?> ignored : records.records(topic)) {
            count++;
        }
        return count;
    }
}
