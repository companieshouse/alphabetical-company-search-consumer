package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service;

import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.logging.LoggingUtils.getLogMap;
import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.logging.LoggingUtils.getRootCause;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.exception.NonRetryableException;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.exception.RetryableException;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.util.ServiceParameters;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.logging.Logger;

/**
 * Service that converts the <code>stream-company-profile/code> Kafka message it receives into a
 * REST request it dispatches to update the ElasticSearch alphabetical company search index.
 */
@Component
public class AlphabeticalIndexUpdaterService implements Service {

    private final Logger logger;

    private AlphabeticalIndexDeleteService alphabeticalIndexDeleteService;

    public AlphabeticalIndexUpdaterService(Logger logger, AlphabeticalIndexDeleteService alphabeticalIndexDeleteService) {
        this.alphabeticalIndexDeleteService = alphabeticalIndexDeleteService;
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

        try {
            var messageType = message.getEvent().getType();
            message.getResourceId();

            switch (messageType) {
                case "changed":
                    logger.debug("This is a 'changed' type message.");
                    break;
                case "deleted":
                    logger.debug("This is a 'deleted' type message.");
                    alphabeticalIndexDeleteService.deleteCompanyFromAlphabeticalIndex(message.getResourceId());
                    break;
                default:
                    logger.error(String.format("NonRetryable error occurred, unknown message type of %s", messageType));
                    throw new IllegalArgumentException("AlphabeticalIndexUpdaterService unknown message type.");
            }
        }catch (ApiErrorResponseException apiException) {
            logger.error(String.format("Error response from INTERNAL API: %s", apiException));
            throw new RetryableException("Attempting to retry due to failed API response", apiException);
        } catch (Exception exception) {
            final var rootCause = getRootCause(exception);
            logger.error(String.format("NonRetryable error occurred. Error: %s", rootCause));
            throw new NonRetryableException("AlphabeticalIndexUpdaterService.processMessage: ", rootCause);
        }
    }

}
