package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service;

import consumer.exception.NonRetryableErrorException;
import org.springframework.stereotype.Component;

@Component
public class NonRetryableExceptionService implements Service {

    @Override
    public void processMessage(ServiceParameters parameters) {
        throw new NonRetryableErrorException("NonRetryableExceptionService to throw NonRetryableException.class",
            new Exception("Unable to handle message"));
    }
}
