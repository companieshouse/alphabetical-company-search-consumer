package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.exception.UpsertServiceException;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.util.ServiceParameters;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.util.ApiClientUtils.mapMessageToRequest;
import uk.gov.companieshouse.logging.Logger;

@Component
public class UpsertService {

    private final ApiClientService apiClientService;

    private final Logger logger;

    public UpsertService(ApiClientService apiClientService, Logger logger) {
        this.apiClientService = apiClientService;
        this.logger = logger;
    }

    public void upsertService(ServiceParameters parameters)
            throws ApiErrorResponseException, URIValidationException, UpsertServiceException{
        String companyNumber = parameters.getData().getResourceId();
        String companyResourceUri = parameters.getData().getResourceUri();
        String resourceUri = String.format("/alphabetical-search/companies/%s", companyNumber);
        CompanyProfileApi companyProfileApi = mapMessageToRequest(parameters);
        logger.info("Upserting company profile. Company number: " + companyNumber + ", Resource URI: "
                + companyResourceUri);

        try {
            apiClientService
                    .getInternalApiClient()
                    .privateSearchResourceHandler()
                    .alphabeticalCompanySearch()
                    .put(resourceUri, companyProfileApi)
                    .execute();
            System.out.println("execute successful");

            // return response;
        } catch (ApiErrorResponseException e) {
            // Log error message and throw it again
            logger.error("Error occurred during upsert request. Company number: " + companyNumber + ", Resource URI: "
                    + companyResourceUri, e);
            throw e;
        }
    }
}
