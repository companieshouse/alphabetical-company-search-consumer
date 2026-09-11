package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.search.PrivateSearchResourceHandler;
import uk.gov.companieshouse.api.handler.search.alphabeticalCompany.PrivateAlphabeticalCompanySearchHandler;
import uk.gov.companieshouse.api.handler.search.alphabeticalCompany.request.PrivateAlphabeticalCompanySearchDelete;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.logging.Logger;

@ExtendWith(MockitoExtension.class)
public class AlphabeticalIndexDeleteServiceTest {

    @Mock
    private Logger logger;

    @Mock
    private ApiClientService apiClientService;

    @Mock
    private Supplier<InternalApiClient> internalApiClientSupplier;

    @Mock
    private InternalApiClient internalApiClient;

    @Mock
    private PrivateSearchResourceHandler privateSearchResourceHandler;

    @Mock
    private PrivateAlphabeticalCompanySearchHandler privateAlphabeticalCompanySearchHandler;

    @Mock
    private PrivateAlphabeticalCompanySearchDelete privateAlphabeticalCompanySearchDelete;

    @InjectMocks
    private AlphabeticalIndexDeleteService underTest;

    @BeforeEach
    void setUp() throws ApiErrorResponseException, URIValidationException {
        when(apiClientService.getInternalApiClient()).thenReturn(internalApiClientSupplier);
        when(internalApiClientSupplier.get()).thenReturn(internalApiClient);
        when(internalApiClient.privateSearchResourceHandler()).thenReturn(privateSearchResourceHandler);
        when(privateSearchResourceHandler.alphabeticalCompanySearch()).thenReturn(privateAlphabeticalCompanySearchHandler);
        when(privateAlphabeticalCompanySearchHandler.delete(anyString())).thenReturn(privateAlphabeticalCompanySearchDelete);
        when(privateAlphabeticalCompanySearchDelete.execute()).thenReturn(new ApiResponse<>(200, null));
    }

    @Test
    void deleteCompanyFromAlphabeticalIndexSuccessfulDeletion() throws Exception {
        String resourceId = "123456";
        underTest.deleteCompany(resourceId);

        verify(logger).info("deleteCompany(resourceId=%s) method called.".formatted(resourceId));
        verify(apiClientService.getInternalApiClient().get().privateSearchResourceHandler().alphabeticalCompanySearch(),
                times(1)).delete("/alphabetical-search/companies/%s".formatted(resourceId));
    }

    @Test
    void deleteCompanyFromAlphabeticalIndexExceptionThrown() throws Exception {
        //Simple test to ensure the error is propagated up to the calling service to be handled
        String resourceId = "123456";
        underTest.deleteCompany(resourceId);

        when(privateAlphabeticalCompanySearchDelete.execute()).thenThrow(ApiErrorResponseException.class);
        assertThrows(ApiErrorResponseException.class, () -> underTest.deleteCompany(resourceId));
    }
}