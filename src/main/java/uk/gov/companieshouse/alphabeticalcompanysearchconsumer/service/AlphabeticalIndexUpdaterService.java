package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service;

import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.logging.LoggingUtils.getLogMap;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.exception.NonRetryableException;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.exception.RetryableException;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.util.ServiceParameters;
import uk.gov.companieshouse.logging.Logger;

/**
 * Service that converts the <code>stream-company-profile/code> Kafka message it receives into a
 * REST request it dispatches to update the ElasticSearch alphabetical company search index.
 */
@Component
public class AlphabeticalIndexUpdaterService implements Service {

    private final Logger logger;
    private final UpsertService upsertService;

    public AlphabeticalIndexUpdaterService(Logger logger, UpsertService upsertService) {
        this.logger = logger;
        this.upsertService = upsertService;
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

            try {
                upsertService.upsertService(parameters);
            } catch (RetryableException apiException) {
                logger.error("Attempting retry due to failed response", apiException);
                throw apiException; // Let it propagate as it's already a RetryableException
            } catch (NonRetryableException uriException) {
                logger.error("NonRetryable Error: " + uriException);
                throw uriException; // Let it propagate as it's already a NonRetryableException
            } catch (Exception exception) {
                logger.error("Unknown error occurred: " + exception.getMessage(), exception);
                throw new NonRetryableException("AlphabeticalIndexUpdaterService.processMessage: Unknown error", exception);
            }
    }

}

