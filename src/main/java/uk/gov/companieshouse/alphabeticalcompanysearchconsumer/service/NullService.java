package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.util.ServiceParameters;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.exception.NonRetryableException;

/**
 * The default service.
 */
@Component
class NullService implements Service {

    @Override
    public void processMessage(ServiceParameters parameters) {
        throw new NonRetryableException("Unable to handle message");
    }
}