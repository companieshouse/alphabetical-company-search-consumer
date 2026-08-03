package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.utils.TestConstants.UPDATE;
import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.utils.TestUtils.ERROR_TOPIC;
import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.utils.TestUtils.INVALID_TOPIC;
import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.utils.TestUtils.MAIN_TOPIC;
import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.utils.TestUtils.RETRY_TOPIC;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service.Service;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.utils.TestUtils;
import uk.gov.companieshouse.stream.ResourceChangedData;

@SpringBootTest
@ActiveProfiles("test_main_positive")
class ConsumerPositiveIT extends AbstractKafkaIntegrationTest {

    @Autowired
    private KafkaProducer<String, ResourceChangedData> testProducer;

    @Autowired
    private KafkaConsumer<String, ResourceChangedData> testConsumer;

    @Autowired
    private CountDownLatch latch;

    @Autowired
    private Service service;

    @BeforeEach
    public void setup() {
        testConsumer.poll(Duration.ofSeconds(1));
    }

    @Test
    void testConsumeFromMainTopic() throws Exception {
        ProducerRecord<String, ResourceChangedData> producerRecord = new ProducerRecord<>(MAIN_TOPIC, 0,
                System.currentTimeMillis(), "key", UPDATE);

        testProducer.send(producerRecord);

        if (!latch.await(5L, TimeUnit.SECONDS)) {
            fail("Timed out waiting for latch");
        }

        ConsumerRecords<?, ?> consumerRecords = KafkaTestUtils.getRecords(testConsumer, Duration.ofSeconds(10), 1);

        //then
        assertThat(TestUtils.noOfRecordsForTopic(consumerRecords, MAIN_TOPIC)).isEqualTo(1);
        assertThat(TestUtils.noOfRecordsForTopic(consumerRecords, RETRY_TOPIC)).isZero();
        assertThat(TestUtils.noOfRecordsForTopic(consumerRecords, ERROR_TOPIC)).isZero();
        assertThat(TestUtils.noOfRecordsForTopic(consumerRecords, INVALID_TOPIC)).isZero();
    }
}

