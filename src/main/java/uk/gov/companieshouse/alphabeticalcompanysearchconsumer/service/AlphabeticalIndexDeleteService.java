package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service;

import java.util.function.Supplier;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.search.PrivateSearchResourceHandler;
import uk.gov.companieshouse.api.handler.search.alphabeticalCompany.PrivateAlphabeticalCompanySearchHandler;
import uk.gov.companieshouse.api.handler.search.alphabeticalCompany.request.PrivateAlphabeticalCompanySearchDelete;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.logging.Logger;

@Component
public class AlphabeticalIndexDeleteService {

    private final Logger logger;
    private final ApiClientService apiClientService;

    public AlphabeticalIndexDeleteService(Logger logger, ApiClientService apiClientService) {
        this.logger = logger;
        this.apiClientService = apiClientService;
    }

    public void deleteCompany(final String resourceId) throws ApiErrorResponseException, URIValidationException {
        logger.info("deleteCompany(resourceId=%s) method called.".formatted(resourceId));

        String resourceUri = String.format("/alphabetical-search/companies/%s", resourceId);
        logger.info("Delete URI is: %s".formatted(resourceUri));

        try {
            Supplier<InternalApiClient> apiClientSupplier = apiClientService.getInternalApiClient();
            InternalApiClient apiClient = apiClientSupplier.get();
            PrivateSearchResourceHandler resourceHandler = apiClient.privateSearchResourceHandler();
            PrivateAlphabeticalCompanySearchHandler searchHandler = resourceHandler.alphabeticalCompanySearch();
            PrivateAlphabeticalCompanySearchDelete searchDelete = searchHandler.delete(resourceUri);

            ApiResponse<Void> apiResponse = searchDelete.execute();

            logger.debug("API Response: [Status Code: %d, Errors: %d]...".formatted(
                    apiResponse.getStatusCode(), apiResponse.getErrors().size()));

        } catch (ApiErrorResponseException e) {
            // Log error message and throw it again
            logger.error("Error occurred during delete request. Company number: " + resourceId +
                    ", Resource URI: " + resourceUri, e);
            throw e;
        }
    }
}
