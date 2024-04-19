package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service;

import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.logging.LoggingUtils.getLogMap;
import static com.fasterxml.jackson.databind.util.ClassUtil.getRootCause;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.exception.NonRetryableException;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.exception.RetryableException;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.util.ServiceParameters;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.ApiResponse;
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
                final ApiResponse<Void> upsertAlphabeticalResponse = upsertService.upsertService(parameters);
                logger.info("API returned response: " + upsertAlphabeticalResponse.getStatusCode());
            } catch (ApiErrorResponseException apiException) {
                logger.error("Error response from INTERNAL API: " + apiException);
                throw new RetryableException("Attempting retry due to failed response", apiException);
            } catch (URIValidationException uriException) {
                logger.error("Error with URI: " + uriException);
                throw new RetryableException("Attempting retry due to URI validation error", uriException);
            } catch (Exception exception) {
                final var rootCause = getRootCause(exception);
                logger.error("NonRetryable Error: " + rootCause);
                throw new NonRetryableException("AlphabeticalIndexUpdaterService.processMessage: ", rootCause);
            }    

    }

}
