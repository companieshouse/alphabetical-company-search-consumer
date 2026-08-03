package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.exception.RetryableException;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.util.ServiceParameters;

@Component
public class RetryableExceptionService implements Service {

    @Override
    public void processMessage(ServiceParameters parameters) {
        throw new RetryableException("Unable to handle message", new Exception("Unable to handle message"));
    }

}