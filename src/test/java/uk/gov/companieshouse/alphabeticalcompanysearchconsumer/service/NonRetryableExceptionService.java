package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.exception.NonRetryableException;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.util.ServiceParameters;

@Component
public class NonRetryableExceptionService implements Service {

    @Override
    public void processMessage(ServiceParameters parameters) {
        throw new NonRetryableException("Unable to handle message",
            new Exception("Unable to handle message"));
    }
}
