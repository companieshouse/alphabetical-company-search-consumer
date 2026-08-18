package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service;

import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.util.ApiClientUtils.mapMessageToRequest;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.config.ApiProperties;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.exception.UpsertServiceException;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.util.ServiceParameters;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.search.PrivateSearchResourceHandler;
import uk.gov.companieshouse.api.handler.search.alphabeticalCompany.PrivateAlphabeticalCompanySearchHandler;
import uk.gov.companieshouse.api.handler.search.alphabeticalCompany.request.PrivateAlphabeticalCompanySearchUpsert;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.logging.Logger;

@Component
public class UpsertService {

    private final ApiClientService apiClientService;
    private final Logger logger;
    private final ApiProperties apiProperties;

    public UpsertService(ApiClientService apiClientService, Logger logger, ApiProperties apiProperties) {
        this.apiClientService = apiClientService;
        this.logger = logger;
        this.apiProperties = apiProperties;
    }

    public void upsertService(ServiceParameters parameters)
            throws ApiErrorResponseException, URIValidationException, UpsertServiceException{

        String companyNumber = parameters.getData().getResourceId();
        String companyResourceUri = parameters.getData().getResourceUri();
        String resourceUri = String.format("%s/%s", apiProperties.alphabeticalSearchUri(), companyNumber);
        CompanyProfileApi companyProfileApi = mapMessageToRequest(parameters);

        logger.info("Upserting company profile. Company number: " + companyNumber + ", Resource URI: "
                + companyResourceUri + ", Upsert URI: " + resourceUri);

        try {
            InternalApiClient client = apiClientService.getInternalApiClient();
            PrivateSearchResourceHandler resourceHandler = client.privateSearchResourceHandler();
            PrivateAlphabeticalCompanySearchHandler searchHandler = resourceHandler.alphabeticalCompanySearch();
            PrivateAlphabeticalCompanySearchUpsert searchUpsert = searchHandler.put(resourceUri, companyProfileApi);

            ApiResponse<Void> apiResponse = searchUpsert.execute();

            logger.info("Upserting company profile: ApiResponse(Status Code=%d) ".formatted(apiResponse.getStatusCode()));

        } catch (ApiErrorResponseException e) {
            // Log error message and throw it again
            logger.error("Error occurred during upsert request. Company number: " + companyNumber + ", Resource URI: "
                    + companyResourceUri, e);
            throw e;
        }
    }
}
