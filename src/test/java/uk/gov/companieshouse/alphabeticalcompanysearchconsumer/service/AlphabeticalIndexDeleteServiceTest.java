package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.search.PrivateSearchResourceHandler;
import uk.gov.companieshouse.api.handler.search.alphabeticalCompany.PrivateAlphabeticalCompanySearchHandler;
import uk.gov.companieshouse.api.handler.search.alphabeticalCompany.request.PrivateAlphabeticalCompanySearchDelete;
import uk.gov.companieshouse.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlphabeticalIndexDeleteServiceTest {

    @Mock
    private Logger logger;

    @Mock
    private ApiClientService apiClientService;
    @Mock
    private InternalApiClient internalApiClient;

    @Mock
    private PrivateSearchResourceHandler privateSearchResourceHandler;

    @Mock
    private PrivateAlphabeticalCompanySearchHandler privateAlphabeticalCompanySearchHandler;

    @Mock
    private PrivateAlphabeticalCompanySearchDelete privateAlphabeticalCompanySearchDelete;

    @InjectMocks
    private AlphabeticalIndexDeleteService alphabeticalIndexDeleteService;

    @BeforeEach
    void setUp() {
        when(apiClientService.getInternalApiClient()).thenReturn(internalApiClient);
        when(internalApiClient.privateSearchResourceHandler()).thenReturn(privateSearchResourceHandler);
        when(privateSearchResourceHandler.alphabeticalCompanySearch()).thenReturn(privateAlphabeticalCompanySearchHandler);
        when(privateAlphabeticalCompanySearchHandler.delete(anyString())).thenReturn(privateAlphabeticalCompanySearchDelete);
    }

    @Test
    void deleteCompanyFromAlphabeticalIndex_SuccessfulDeletion() throws Exception {
        String resourceId = "123456";
        alphabeticalIndexDeleteService.deleteCompanyFromAlphabeticalIndex(resourceId);

        verify(logger).info("Deleting " + resourceId + " from Alphabetical index!");
        verify(apiClientService.getInternalApiClient().privateSearchResourceHandler().alphabeticalCompanySearch(), times(1)).delete("/alphabetical-search/companies/" + resourceId);
    }

    @Test
    void deleteCompanyFromAlphabeticalIndex_ExceptionThrown() throws Exception {
        //Simple test to ensure the error is propagated up to the calling service to be handled
        String resourceId = "123456";
        alphabeticalIndexDeleteService.deleteCompanyFromAlphabeticalIndex(resourceId);

        when(privateAlphabeticalCompanySearchDelete.execute()).thenThrow(ApiErrorResponseException.class);
        assertThrows(ApiErrorResponseException.class, () -> alphabeticalIndexDeleteService.deleteCompanyFromAlphabeticalIndex(resourceId));
    }
}