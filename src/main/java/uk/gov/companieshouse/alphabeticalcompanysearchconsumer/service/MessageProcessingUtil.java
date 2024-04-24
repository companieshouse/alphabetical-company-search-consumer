package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service;

import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.logging.LoggingUtils.getLogMap;
import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.logging.LoggingUtils.getRootCause;
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
public class MessageProcessingUtil implements Service {

    private final Logger logger;
    private final UpsertService upsertService;

    public MessageProcessingUtil(Logger logger, UpsertService upsertService) {
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
                var messageType = message.getEvent().getType();
                message.getResourceId();
                switch (messageType) {
                    case "changed":
                        logger.debug("This is a 'changed' type message.");
                        upsertService.upsertService(parameters);
                        break;
                    default:
            logger.error(String.format("NonRetryable error occurred, unknown message type of %s", messageType));
            throw new IllegalArgumentException("MessageProcessingUtil unknown message type.");      
                }
            } catch (ApiErrorResponseException apiException) {
                logger.error(String.format("Error response from INTERNAL API: %s", apiException));
                throw new RetryableException("Attempting to retry due to failed API response", apiException); // Let it propagate as it's already a RetryableException
            } catch (Exception exception) {
                final var rootCause = getRootCause(exception);
                logger.error("Unknown error occurred: " + exception.getMessage(), exception);
                throw new NonRetryableException("MessageProcessingUtil.processMessage: ", rootCause);
            }
    }
}