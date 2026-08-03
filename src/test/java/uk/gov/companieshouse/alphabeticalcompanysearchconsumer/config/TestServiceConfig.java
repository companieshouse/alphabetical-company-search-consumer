package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service.NonRetryableExceptionService;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service.Service;

@TestConfiguration
public class TestServiceConfig {

    @Bean("nonRetryableExceptionService")
    @Primary
    Service nonRetryableExceptionService() {
        return new NonRetryableExceptionService();
    }

}
