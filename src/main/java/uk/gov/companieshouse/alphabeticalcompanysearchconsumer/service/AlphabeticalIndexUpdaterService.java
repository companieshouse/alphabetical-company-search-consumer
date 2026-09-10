package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service;

import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.logging.LoggingUtils.getLogMap;
import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.logging.LoggingUtils.getRootCause;

import consumer.exception.NonRetryableErrorException;
import consumer.exception.RetryableErrorException;
import org.springframework.stereotype.Component;
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
    private final AlphabeticalIndexUpsertService upsertService;
    private final AlphabeticalIndexDeleteService deleteService;

    public AlphabeticalIndexUpdaterService(Logger logger, AlphabeticalIndexDeleteService deleteService, AlphabeticalIndexUpsertService upsertService) {
        this.logger = logger;
        this.deleteService = deleteService;
        this.upsertService = upsertService;
    }

    @Override
    public void processMessage(final ServiceParameters parameters) {
        logger.info("processMessage(kind=%s) method called.".formatted(
                parameters.getData().getResourceKind()), getLogMap(parameters.getData()));

        final var message = parameters.getData();
        final var resourceId = message.getResourceId();
        final var resourceKind = message.getResourceKind();
        final var resourceUri = message.getResourceUri();

        logger.info("Processing message " + message + " for resource ID " + resourceId +
            ", resource kind " + resourceKind + ", resource URI " + resourceUri + ".", getLogMap(message));

        try {
            var messageType = message.getEvent().getType();

            switch (messageType) {
                case "changed" -> {
                    logger.debug("This is a 'changed' type message.");
                    upsertService.upsertCompany(parameters);
                }
                case "deleted" -> {
                    logger.debug("This is a 'deleted' type message.");
                    deleteService.deleteCompany(resourceId);
                }
                default -> {
                    logger.error(String.format("NonRetryable error occurred, unknown message type of %s", messageType));
                    throw new IllegalArgumentException("AlphabeticalIndexUpdaterService unknown message type.");
                }
            }

        } catch (ApiErrorResponseException apiException) {
            logger.error(String.format("Error response from INTERNAL API: %s", apiException));
            throw new RetryableErrorException("Attempting to retry due to failed API response", apiException);

        } catch (Exception exception) {
            final var rootCause = getRootCause(exception);
            logger.error(String.format("NonRetryable error occurred. Error: %s", rootCause));
            throw new NonRetryableErrorException("AlphabeticalIndexUpdaterService.processMessage: ", exception);
        }
    }
}