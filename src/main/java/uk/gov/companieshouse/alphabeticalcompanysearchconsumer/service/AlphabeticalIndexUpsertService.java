package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service;

import java.util.function.Supplier;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.mapper.CompanyProfileMapper;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.search.PrivateSearchResourceHandler;
import uk.gov.companieshouse.api.handler.search.alphabeticalCompany.PrivateAlphabeticalCompanySearchHandler;
import uk.gov.companieshouse.api.handler.search.alphabeticalCompany.request.PrivateAlphabeticalCompanySearchUpsert;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.stream.ResourceChangedData;

@Component
public class AlphabeticalIndexUpsertService {

    private final ApiClientService apiClientService;
    private final Logger logger;
    private final CompanyProfileMapper mapper;

    public AlphabeticalIndexUpsertService(ApiClientService apiClientService, Logger logger, CompanyProfileMapper mapper) {
        this.apiClientService = apiClientService;
        this.logger = logger;
        this.mapper = mapper;
    }

    public void upsertCompany(final ServiceParameters parameters)
            throws ApiErrorResponseException, URIValidationException{
        logger.info("upsertCompany(resourceId=%s) method called.".formatted(parameters.getData().getResourceId()));

        ResourceChangedData data = parameters.getData();

        String companyNumber = data.getResourceId();
        String companyResourceUri = data.getResourceUri();
        String resourceUri = String.format("/alphabetical-search/companies/%s", companyNumber);

        CompanyProfileApi companyProfileApi = mapper.mapToCompanyProfile(data.getData());

        logger.info("Upserting company profile. Company number: " + companyNumber + ", Resource URI: "
                + companyResourceUri + ", Upsert URI: " + resourceUri);

        try {
            Supplier<InternalApiClient> apiClientSupplier = apiClientService.getInternalApiClient();
            InternalApiClient client = apiClientSupplier.get();
            PrivateSearchResourceHandler resourceHandler = client.privateSearchResourceHandler();
            PrivateAlphabeticalCompanySearchHandler searchHandler = resourceHandler.alphabeticalCompanySearch();
            PrivateAlphabeticalCompanySearchUpsert searchUpsert = searchHandler.put(resourceUri, companyProfileApi);

            ApiResponse<Void> apiResponse = searchUpsert.execute();

            logger.info("Upserting company profile: ApiResponse(Status Code=%d) ".formatted(apiResponse.getStatusCode()));

        } catch (ApiErrorResponseException e) {
            // Log error message and throw it again
            logger.error("Error occurred during upsert request. Company number: " + companyNumber +
                    ", Resource URI: " + companyResourceUri, e);
            throw e;
        }
    }
}
