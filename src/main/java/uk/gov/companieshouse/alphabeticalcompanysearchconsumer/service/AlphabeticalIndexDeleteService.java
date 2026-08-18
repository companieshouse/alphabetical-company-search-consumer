package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.config.ApiProperties;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.logging.Logger;

@Component
public class AlphabeticalIndexDeleteService {

    private final Logger logger;
    private final ApiClientService apiClientService;
    private final ApiProperties apiProperties;

    public AlphabeticalIndexDeleteService(Logger logger, ApiClientService apiClientService,
            ApiProperties apiProperties) {
        this.logger = logger;
        this.apiClientService = apiClientService;
        this.apiProperties = apiProperties;
    }

    public void deleteCompanyFromAlphabeticalIndex(String resourceId) throws ApiErrorResponseException, URIValidationException {
        logger.info("Deleting " + resourceId + " from Alphabetical index!");
        String resourceUri = String.format("%s/%s", apiProperties.alphabeticalSearchUri(), resourceId);
        logger.info("Delete URI is: " + resourceUri);
        apiClientService
                .getInternalApiClient()
                .privateSearchResourceHandler()
                .alphabeticalCompanySearch()
                .delete(resourceUri).execute();
    }
}
