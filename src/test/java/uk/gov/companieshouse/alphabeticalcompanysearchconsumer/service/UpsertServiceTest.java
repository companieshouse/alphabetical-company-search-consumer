package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.utils.TestConstants.UPDATE;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;
import java.util.Map;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.mapper.CompanyProfileMapper;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.util.ServiceParameters;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.search.PrivateSearchResourceHandler;
import uk.gov.companieshouse.api.handler.search.alphabeticalCompany.PrivateAlphabeticalCompanySearchHandler;
import uk.gov.companieshouse.api.handler.search.alphabeticalCompany.request.PrivateAlphabeticalCompanySearchUpsert;
import uk.gov.companieshouse.api.http.HttpClient;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.logging.Logger;

@ExtendWith(MockitoExtension.class)
class UpsertServiceTest {

    private static final ServiceParameters parameters = new ServiceParameters(UPDATE);

    @Mock
    private Logger logger;

    @Mock
    private ApiClientService apiClientService;

    @Mock
    private CompanyProfileMapper companyProfileMapper;

    @Mock
    private Supplier<InternalApiClient> internalApiClientSupplier;

    @Mock
    private InternalApiClient internalApiClient;

    @Mock
    private HttpClient httpClient;

    @Mock
    private PrivateSearchResourceHandler privateSearchResourceHandler;

    @Mock
    private PrivateAlphabeticalCompanySearchHandler privateAlphabeticalCompanySearchHandler;

    @Mock
    private PrivateAlphabeticalCompanySearchUpsert privateAlphabeticalCompanySearchUpsert;

    @Mock
    private CompanyProfileApi companyProfileApi;

    @InjectMocks
    private AlphabeticalIndexUpsertService underTest;

    @BeforeEach
    void setUp() {
        when(companyProfileMapper.mapToCompanyProfile(anyString())).thenReturn(companyProfileApi);
        when(apiClientService.getInternalApiClient()).thenReturn(internalApiClientSupplier);
        when(internalApiClientSupplier.get()).thenReturn(internalApiClient);
        when(internalApiClient.privateSearchResourceHandler()).thenReturn(privateSearchResourceHandler);
        when(privateSearchResourceHandler.alphabeticalCompanySearch()).thenReturn(privateAlphabeticalCompanySearchHandler);
    }

    @Test
    @DisplayName("Should upsert company profile successfully with no exceptions")
    void upsertService_Successful() throws ApiErrorResponseException, URIValidationException {
        // given
        String companyNumber = "00006400";

        when(privateAlphabeticalCompanySearchHandler.put(any(), any(CompanyProfileApi.class)))
                .thenReturn(privateAlphabeticalCompanySearchUpsert);
        when(privateAlphabeticalCompanySearchUpsert.execute()).thenReturn(
                new ApiResponse<>(200, Map.of()));

        // when
        underTest.upsertCompany(parameters);

        // then
        verify(privateAlphabeticalCompanySearchHandler).put(
                eq("/alphabetical-search/companies/" + companyNumber), any(CompanyProfileApi.class));
    }

    @Test
    @DisplayName("Should delegate to response handler when ApiErrorResponseException (503) caught during upsert")
    void upsertService_ApiErrorResponseException() throws Exception {
        // given
        String companyNumber = "00006400";

        HttpResponseException.Builder builder = new HttpResponseException.Builder(
                503, "service unavailable", new HttpHeaders());
        ApiErrorResponseException apiErrorResponseException = new ApiErrorResponseException(builder);

        when(privateAlphabeticalCompanySearchHandler.put(any(), any(CompanyProfileApi.class))).thenReturn(privateAlphabeticalCompanySearchUpsert);
        when(privateAlphabeticalCompanySearchUpsert.execute()).thenThrow(apiErrorResponseException);
        try {
            // when
            underTest.upsertCompany(parameters);

        } catch (ApiErrorResponseException e) {

            // then
            verify(privateAlphabeticalCompanySearchHandler).put(
                    eq("/alphabetical-search/companies/" + companyNumber), any(CompanyProfileApi.class));

        }
    }
}