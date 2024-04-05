package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.logging.DataMapHolder;

@Component
public class SearchApiClient {

    @Value("${api.api-url}")
    private String apiUrl;

    // private final String apiUrl;
    private static final String PUT_COMPANY_PROFILE_FAILED_MSG = "Failed in PUT company profile list to resource URI %s";
    private static final String PUT_COMPANY_PROFILE_ERROR_MSG = "Error [%s] in PUT company profile to resource URI %s";
    
    private final Supplier<InternalApiClient> internalApiClientFactory;
    private final ResponseHandler responseHandler;

    public SearchApiClient(Supplier<InternalApiClient> internalApiClientFactory,
            ResponseHandler responseHandler) {
        this.internalApiClientFactory = internalApiClientFactory;
        this.responseHandler = responseHandler;
    }

    public void upsertCompanyProfile(String companyNumber, CompanyProfileApi companyProfileApi) {
        String resourceUri = apiUrl + String.format("/alphabetical-search/companies/%s", companyNumber);
        System.out.println("resourceuri is " + resourceUri);
        System.out.println("company number is " + companyProfileApi.getCompanyNumber());
        InternalApiClient apiClient = internalApiClientFactory.get();
        apiClient.getHttpClient().setRequestId(DataMapHolder.getRequestId());
        try {
            apiClient.privateSearchResourceHandler()
                    .alphabeticalCompanySearch()
                    .put(resourceUri, companyProfileApi)
                    .execute();
            System.out.println("its been executed");        
        } catch (ApiErrorResponseException ex) {
            System.out.println("1111111 " + ex.getMessage());        
            responseHandler.handle(
                    String.format(PUT_COMPANY_PROFILE_ERROR_MSG, ex.getStatusCode(), resourceUri), ex);
        } catch (IllegalArgumentException ex) {
            System.out.println("222222 " + ex.getMessage());        
            responseHandler.handle(String.format(PUT_COMPANY_PROFILE_FAILED_MSG, resourceUri), ex);
        } catch (URIValidationException ex) {
            System.out.println("333333 " + ex.getMessage());        
            responseHandler.handle(String.format(PUT_COMPANY_PROFILE_FAILED_MSG, resourceUri), ex);
        }
    }


}

