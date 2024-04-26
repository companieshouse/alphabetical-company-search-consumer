package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.logging.Logger;

@Component
public class AlphabeticalIndexDeleteService {

    private final Logger logger;
    private final ApiClientService apiClientService;

    public AlphabeticalIndexDeleteService(Logger logger, ApiClientService apiClientService) {
        this.logger = logger;
        this.apiClientService = apiClientService;
    }

    public void deleteCompanyFromAlphabeticalIndex(String resourceId) throws ApiErrorResponseException, URIValidationException {
        logger.info("Deleting " + resourceId + " from Alphabetical index!");
        apiClientService
                .getInternalApiClient()
                .privateSearchResourceHandler()
                .alphabeticalCompanySearch()
                .delete("/alphabetical-search/companies/" + resourceId).execute();
    }
}
