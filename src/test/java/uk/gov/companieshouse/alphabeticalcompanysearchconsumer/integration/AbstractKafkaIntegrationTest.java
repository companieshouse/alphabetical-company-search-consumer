package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.integration;

import java.time.Duration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.config.TestKafkaConfig;

@Testcontainers
@Import(TestKafkaConfig.class)
abstract class AbstractKafkaIntegrationTest {

    @Container
    protected static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse(
            "confluentinc/cp-kafka:latest"))
            .withStartupTimeout(Duration.ofMinutes(2)) // increase from default 60 seconds
            .withKraft();

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

}