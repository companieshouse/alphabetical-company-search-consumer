package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service;

import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.logging.LoggingUtils.getLogMap;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.util.ServiceParameters;
import uk.gov.companieshouse.logging.Logger;

/**
 * Service that converts the <code>stream-company-profile/code> Kafka message it receives into a
 * REST request it dispatches to update the ElasticSearch alphabetical company search index.
 */
@Component
public class AlphabeticalIndexUpdaterService implements Service {

    private final Logger logger;

    public AlphabeticalIndexUpdaterService(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void processMessage(ServiceParameters parameters) {

        final var message = parameters.getData();
        final var resourceId = message.getResourceId();
        final var resourceKind = message.getResourceKind();
        final var resourceUri = message.getResourceUri();

        logger.info("Processing message " + message + " for resource ID " + resourceId +
            ", resource kind " + resourceKind + ", resource URI " + resourceUri + ".",
            getLogMap(message));

    }

}
