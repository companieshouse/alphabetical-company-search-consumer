package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.config;

import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.utils.TestUtils.ERROR_TOPIC;
import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.utils.TestUtils.INVALID_TOPIC;
import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.utils.TestUtils.MAIN_TOPIC;
import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.utils.TestUtils.RETRY_TOPIC;

import consumer.deserialization.AvroDeserializer;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service.NonRetryableExceptionService;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service.Service;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.kafka.serialization.SerializerFactory;
import uk.gov.companieshouse.stream.ResourceChangedData;

@TestConfiguration
public class TestKafkaConfig {

    @Bean
    CountDownLatch latch(@Value("${steps}") int steps) {
        return new CountDownLatch(steps);
    }

    @Bean
    KafkaConsumer<String, ResourceChangedData> testConsumer(
        @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        KafkaConsumer<String, ResourceChangedData> kafkaConsumer = new KafkaConsumer<>(
            Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false",
                ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString()),
            new StringDeserializer(), new AvroDeserializer<>(ResourceChangedData.class));
        kafkaConsumer.subscribe(List.of(MAIN_TOPIC, ERROR_TOPIC, RETRY_TOPIC,
            INVALID_TOPIC));
        return kafkaConsumer;
    }

    @Bean
    KafkaProducer<String, ResourceChangedData> testProducer(
        @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        return new KafkaProducer<>(
            Map.of(
                ProducerConfig.ACKS_CONFIG, "all",
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers),
            new StringSerializer(),
            (topic, data) -> {
                try {
                    return new SerializerFactory()
                        .getSpecificRecordSerializer(ResourceChangedData.class).toBinary(data);
                } catch (SerializationException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    @Bean
    @Primary
    public Service getService() {
        return new NonRetryableExceptionService();
    }
}