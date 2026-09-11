package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.utils.TestConstants.UPDATE;
import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.utils.TestUtils.ERROR_TOPIC;
import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.utils.TestUtils.INVALID_TOPIC;
import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.utils.TestUtils.MAIN_TOPIC;
import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.utils.TestUtils.RETRY_TOPIC;
import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.utils.TestUtils.noOfRecordsForTopic;

import consumer.exception.NonRetryableErrorException;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.config.TestApplicationConfig;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.config.TestServiceConfig;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.serialization.ResourceChangedDataSerializer;
import uk.gov.companieshouse.stream.ResourceChangedData;

@SpringBootTest
@ActiveProfiles("test_main_nonretryable")
@Import({TestApplicationConfig.class, TestServiceConfig.class})
class ProducerSerializationExceptionIT extends AbstractKafkaIntegrationTest {

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        public ResourceChangedDataSerializer serializer() {
            return Mockito.mock(ResourceChangedDataSerializer.class);
        }

    }

    @Autowired
    private KafkaProducer<String, ResourceChangedData> testProducer;

    @Autowired
    private KafkaConsumer<String, ResourceChangedData> testConsumer;

    @Autowired
    private CountDownLatch latch;

    @Autowired
    private ResourceChangedDataSerializer serializer;

    @BeforeEach
    public void drainKafkaTopics() {
        testConsumer.poll(Duration.ofSeconds(1));
    }

    @Test
    @DisplayName("SerializationException producing message to DLT causes looping")
    void testPublishToInvalidMessageTopicSerializationException() throws InterruptedException {

        // given
        // Here we only throw the exception twice to allow the test to complete in much less time.
        // In reality, if such an exception occurred once trying to serialize the message to be
        // produced, it would presumably occur on every serialization/production attempt.
        when(serializer.serialize(MAIN_TOPIC, UPDATE))
                .thenThrow(NonRetryableErrorException.class)
                .thenThrow(NonRetryableErrorException.class)
                .thenReturn(null);

        ProducerRecord<String, ResourceChangedData> producerRecord = new ProducerRecord<>(MAIN_TOPIC, 0,
                System.currentTimeMillis(), "key", UPDATE);

        // when
        testProducer.send(producerRecord);

        if (!latch.await(5L, TimeUnit.SECONDS)) {
            fail("Timed out waiting for latch");
        }

        ConsumerRecords<?, ?> consumerRecords = KafkaTestUtils.getRecords(testConsumer,
            Duration.ofSeconds(10), 2);

        // then
        assertThat(noOfRecordsForTopic(consumerRecords, MAIN_TOPIC)).isEqualTo(1);
        assertThat(noOfRecordsForTopic(consumerRecords, RETRY_TOPIC)).isZero();
        assertThat(noOfRecordsForTopic(consumerRecords, ERROR_TOPIC)).isZero();
        assertThat(noOfRecordsForTopic(consumerRecords, INVALID_TOPIC)).isEqualTo(1);
    }
}
