package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service;

import consumer.exception.RetryableErrorException;
import org.springframework.stereotype.Component;

@Component
public class RetryableExceptionService implements Service {

    @Override
    public void processMessage(ServiceParameters parameters) {
        throw new RetryableErrorException("Unable to handle message", new Exception("Unable to handle message"));
    }

}