package uk.gov.companieshouse.alphabeticalcompanysearchconsumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.TestUtils.ERROR_TOPIC;
import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.TestUtils.INVALID_TOPIC;
import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.TestUtils.MAIN_TOPIC;
import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.TestUtils.RETRY_TOPIC;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service.Service;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.exception.RetryableException;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.util.ServiceParameters;

@SpringBootTest
@ActiveProfiles("test_main_retryable")
class ConsumerRetryableExceptionTest extends AbstractKafkaIntegrationTest {

    @Autowired
    private KafkaProducer<String, String> testProducer;
    @Autowired
    private KafkaConsumer<String, String> testConsumer;

    @Autowired
    private CountDownLatch latch;

    @MockBean
    private Service service;

    @BeforeEach
    public void drainKafkaTopics() {
        testConsumer.poll(Duration.ofSeconds(1));
    }

    @Test
    void testRepublishToErrorTopicThroughRetryTopics() throws InterruptedException {
        //given
        doThrow(RetryableException.class).when(service).processMessage(any());

        //when
        testProducer.send(new ProducerRecord<>(MAIN_TOPIC, 0, System.currentTimeMillis(), "key",
                "value"));
        if (!latch.await(5L, TimeUnit.SECONDS)) {
            fail("Timed out waiting for latch");
        }
        ConsumerRecords<?, ?> consumerRecords = KafkaTestUtils.getRecords(testConsumer, Duration.ofSeconds(10), 6);

        //then
        assertThat(TestUtils.noOfRecordsForTopic(consumerRecords, MAIN_TOPIC)).isEqualTo(1);
        assertThat(TestUtils.noOfRecordsForTopic(consumerRecords, RETRY_TOPIC)).isEqualTo(4);
        assertThat(TestUtils.noOfRecordsForTopic(consumerRecords, ERROR_TOPIC)).isEqualTo(1);
        assertThat(TestUtils.noOfRecordsForTopic(consumerRecords, INVALID_TOPIC)).isZero();
        verify(service, times(5)).processMessage(new ServiceParameters("value"));
    }
}
