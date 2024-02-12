package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service;

import java.util.Map;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.util.ServiceParameters;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.util.DataMap;
import uk.gov.companieshouse.stream.EventRecord;

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
        final var contextId = message.getContextId();
        final var event = message.getEvent();
        final var data = message.getData();

        logger.info("Processing message " + message + " for resource ID " + resourceId +
            ", resource kind " + resourceKind + ", resource URI " + resourceUri + ".",
            getLogMap(resourceId, resourceKind, resourceUri, contextId, event, data));

    }

    private Map<String, Object> getLogMap(
        final String resourceId,
        final String resourceKind,
        final String resourceUri,
        final String contextId,
        final EventRecord event,
        final String data) {
        return new DataMap.Builder()
            .resourceId(resourceId)
            .resourceKind(resourceKind)
            .resourceUri(resourceUri)
            .contextId(contextId)
            .eventRecord(event.toString())
            .data(data)
            .build()
            .getLogMap();
    }
}
