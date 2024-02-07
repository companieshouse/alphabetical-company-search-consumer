package uk.gov.companieshouse.alphabeticalcompanysearchconsumer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service.TestKafkaConfig;

@SpringBootTest
@Import(TestKafkaConfig.class)
@ActiveProfiles("test_main_positive")
class AlphabeticalCompanySearchConsumerApplicationTests {

    @SuppressWarnings("squid:S2699") // at least one assertion
    @DisplayName("Context loads")
    @Test
    void contextLoads() {
    }

}
