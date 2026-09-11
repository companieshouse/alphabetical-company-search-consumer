package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;
import consumer.exception.NonRetryableErrorException;
import consumer.exception.RetryableErrorException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.stream.EventRecord;
import uk.gov.companieshouse.stream.ResourceChangedData;

@ExtendWith(MockitoExtension.class)
class AlphabeticalIndexUpdaterServiceTest {

    private static final String RESOURCE_ID = "12345678";
    private static final String RESOURCE_KIND = "company-profile";
    private static final String RESOURCE_URI = "/company/12345678";

    @Mock
    private Logger logger;

    @Mock
    private AlphabeticalIndexUpsertService upsertService;

    @Mock
    private AlphabeticalIndexDeleteService deleteService;

    @Mock
    private ServiceParameters serviceParameters;

    @Mock
    private ResourceChangedData resourceChangedData;

    @Mock
    private EventRecord eventRecord;

    @InjectMocks
    private AlphabeticalIndexUpdaterService service;

    @Test
    void shouldProcessChangedMessageSuccessfully() throws ApiErrorResponseException, URIValidationException {
        // Arrange
        when(serviceParameters.getData()).thenReturn(resourceChangedData);
        when(resourceChangedData.getResourceKind()).thenReturn(RESOURCE_KIND);
        when(resourceChangedData.getResourceId()).thenReturn(RESOURCE_ID);
        when(resourceChangedData.getResourceUri()).thenReturn(RESOURCE_URI);
        when(resourceChangedData.getEvent()).thenReturn(eventRecord);
        when(eventRecord.getType()).thenReturn("changed");

        // Act
        service.processMessage(serviceParameters);

        // Assert
        verify(upsertService).upsertCompany(serviceParameters);
        verify(logger).debug("This is a 'changed' type message.");
    }

    @Test
    void shouldProcessDeletedMessageSuccessfully() throws ApiErrorResponseException, URIValidationException {
        // Arrange
        when(serviceParameters.getData()).thenReturn(resourceChangedData);
        when(resourceChangedData.getResourceKind()).thenReturn(RESOURCE_KIND);
        when(resourceChangedData.getResourceId()).thenReturn(RESOURCE_ID);
        when(resourceChangedData.getResourceUri()).thenReturn(RESOURCE_URI);
        when(resourceChangedData.getEvent()).thenReturn(eventRecord);
        when(eventRecord.getType()).thenReturn("deleted");

        // Act
        service.processMessage(serviceParameters);

        // Assert
        verify(deleteService).deleteCompany(RESOURCE_ID);
        verify(logger).debug("This is a 'deleted' type message.");
    }

    @Test
    void shouldThrowNonRetryableExceptionForUnknownMessageType() {
        // Arrange
        when(serviceParameters.getData()).thenReturn(resourceChangedData);
        when(resourceChangedData.getResourceKind()).thenReturn(RESOURCE_KIND);
        when(resourceChangedData.getResourceId()).thenReturn(RESOURCE_ID);
        when(resourceChangedData.getResourceUri()).thenReturn(RESOURCE_URI);
        when(resourceChangedData.getEvent()).thenReturn(eventRecord);
        when(eventRecord.getType()).thenReturn("unknown");

        // Act & Assert
        assertThrows(NonRetryableErrorException.class,
                () -> service.processMessage(serviceParameters));

        verify(logger).error("NonRetryable error occurred, unknown message type of unknown");
    }

    @Test
    void shouldThrowRetryableExceptionWhenApiErrorResponseExceptionOccurs() throws Exception {
        // Arrange
        when(serviceParameters.getData()).thenReturn(resourceChangedData);
        when(resourceChangedData.getResourceKind()).thenReturn(RESOURCE_KIND);
        when(resourceChangedData.getResourceId()).thenReturn(RESOURCE_ID);
        when(resourceChangedData.getResourceUri()).thenReturn(RESOURCE_URI);
        when(resourceChangedData.getEvent()).thenReturn(eventRecord);
        when(eventRecord.getType()).thenReturn("changed");

        HttpResponseException httpException = mock(HttpResponseException.class);
        when(httpException.getStatusCode()).thenReturn(500);
        when(httpException.getStatusMessage()).thenReturn("Internal Server Error");
        when(httpException.getHeaders()).thenReturn(new HttpHeaders());

        ApiErrorResponseException apiException = ApiErrorResponseException.fromHttpResponseException(httpException);
        doThrow(apiException).when(upsertService).upsertCompany(any(ServiceParameters.class));

        // Act & Assert
        assertThrows(RetryableErrorException.class,
                () -> service.processMessage(serviceParameters));

        verify(logger).error(
                String.format("Error response from INTERNAL API: %s", apiException));
    }

    @Test
    void shouldThrowNonRetryableExceptionWhenGenericExceptionOccurs()
            throws ApiErrorResponseException, URIValidationException {
        // Arrange
        when(serviceParameters.getData()).thenReturn(resourceChangedData);
        when(resourceChangedData.getResourceKind()).thenReturn(RESOURCE_KIND);
        when(resourceChangedData.getResourceId()).thenReturn(RESOURCE_ID);
        when(resourceChangedData.getResourceUri()).thenReturn(RESOURCE_URI);
        when(resourceChangedData.getEvent()).thenReturn(eventRecord);
        when(eventRecord.getType()).thenReturn("changed");

        RuntimeException exception = new RuntimeException("Generic error");
        doThrow(exception).when(upsertService).upsertCompany(any(ServiceParameters.class));

        // Act & Assert
        assertThrows(NonRetryableErrorException.class,
                () -> service.processMessage(serviceParameters));

        verify(logger).error(anyString());
    }

    @Test
    void shouldThrowNonRetryableExceptionWhenDeleteServiceThrowsException()
            throws ApiErrorResponseException, URIValidationException {
        // Arrange
        when(serviceParameters.getData()).thenReturn(resourceChangedData);
        when(resourceChangedData.getResourceKind()).thenReturn(RESOURCE_KIND);
        when(resourceChangedData.getResourceId()).thenReturn(RESOURCE_ID);
        when(resourceChangedData.getResourceUri()).thenReturn(RESOURCE_URI);
        when(resourceChangedData.getEvent()).thenReturn(eventRecord);
        when(eventRecord.getType()).thenReturn("deleted");

        RuntimeException exception = new RuntimeException("Delete error");
        doThrow(exception).when(deleteService).deleteCompany(anyString());

        // Act & Assert
        assertThrows(NonRetryableErrorException.class,
                () -> service.processMessage(serviceParameters));

        verify(logger).error(anyString());
    }

    @Test
    void shouldThrowRetryableExceptionWhenDeleteServiceThrowsApiErrorResponseException() throws Exception {
        // Arrange
        when(serviceParameters.getData()).thenReturn(resourceChangedData);
        when(resourceChangedData.getResourceKind()).thenReturn(RESOURCE_KIND);
        when(resourceChangedData.getResourceId()).thenReturn(RESOURCE_ID);
        when(resourceChangedData.getResourceUri()).thenReturn(RESOURCE_URI);
        when(resourceChangedData.getEvent()).thenReturn(eventRecord);
        when(eventRecord.getType()).thenReturn("deleted");

        HttpResponseException httpException = mock(HttpResponseException.class);
        when(httpException.getStatusCode()).thenReturn(500);
        when(httpException.getStatusMessage()).thenReturn("Internal Server Error");
        when(httpException.getHeaders()).thenReturn(new HttpHeaders());


        ApiErrorResponseException apiException = ApiErrorResponseException.fromHttpResponseException(httpException);
        doThrow(apiException).when(deleteService).deleteCompany(anyString());

        // Act & Assert
        assertThrows(RetryableErrorException.class,
                () -> service.processMessage(serviceParameters));

        verify(logger).error(
                String.format("Error response from INTERNAL API: %s", apiException));
    }

    @Test
    void shouldLogProcessMessageCalledWithResourceKind() {
        // Arrange
        when(serviceParameters.getData()).thenReturn(resourceChangedData);
        when(resourceChangedData.getResourceKind()).thenReturn(RESOURCE_KIND);
        when(resourceChangedData.getResourceId()).thenReturn(RESOURCE_ID);
        when(resourceChangedData.getResourceUri()).thenReturn(RESOURCE_URI);
        when(resourceChangedData.getEvent()).thenReturn(eventRecord);
        when(eventRecord.getType()).thenReturn("changed");

        // Act
        service.processMessage(serviceParameters);

        // Assert
        verify(logger).info(
                eq("processMessage(kind=%s) method called.".formatted(RESOURCE_KIND)),
                any()
        );
    }
}