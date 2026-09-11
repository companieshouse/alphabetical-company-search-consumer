package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import consumer.exception.NonRetryableErrorException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.logging.DataMapHolder;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.stream.ResourceChangedData;

@ExtendWith(MockitoExtension.class)
class ConsumerTest {

    private static final String RESOURCE_KIND = "filing-history";
    private static final String CONTEXT_ID = "ctx-001";

    @Mock
    private Service service;

    @Mock
    private Logger logger;

    @Mock
    private Message<ResourceChangedData> message;

    @Mock
    private ResourceChangedData resourceChangedData;

    @InjectMocks
    private Consumer consumer;

    @Test
    void shouldConsumeMessageSuccessfully() {
        // Arrange
        when(message.getPayload()).thenReturn(resourceChangedData);
        when(resourceChangedData.getResourceKind()).thenReturn(RESOURCE_KIND);
        when(resourceChangedData.getContextId()).thenReturn(CONTEXT_ID);

        // Act
        consumer.consume(message);

        // Assert
        verify(service).processMessage(any(ServiceParameters.class));
        verify(logger).info(
                "consume(kind=%s) method called.".formatted(RESOURCE_KIND),
                DataMapHolder.getLogMap()
        );
        verify(logger, times(2)).info(
                anyString(),
                eq(DataMapHolder.getLogMap())
        );
    }

    @Test
    void shouldThrowAndLogExceptionWhenServiceThrowsNonRetryableException() {
        // Arrange
        when(message.getPayload()).thenReturn(resourceChangedData);
        when(resourceChangedData.getResourceKind()).thenReturn(RESOURCE_KIND);
        when(resourceChangedData.getContextId()).thenReturn(CONTEXT_ID);

        NonRetryableErrorException exception = new NonRetryableErrorException("Non retryable error");
        doThrow(exception).when(service).processMessage(any(ServiceParameters.class));

        // Act & Assert
        assertThrows(NonRetryableErrorException.class, () -> consumer.consume(message));

        verify(logger).errorContext(
                CONTEXT_ID,
                "Exception occurred while processing message",
                exception,
                DataMapHolder.getLogMap()
        );
    }

    @Test
    void shouldThrowAndLogExceptionWhenServiceThrowsRetryableException() {
        // Arrange
        when(message.getPayload()).thenReturn(resourceChangedData);
        when(resourceChangedData.getResourceKind()).thenReturn(RESOURCE_KIND);
        when(resourceChangedData.getContextId()).thenReturn(CONTEXT_ID);

        RuntimeException exception = new RuntimeException("Retryable error");
        doThrow(exception).when(service).processMessage(any(ServiceParameters.class));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> consumer.consume(message));

        verify(logger).errorContext(
                CONTEXT_ID,
                "Exception occurred while processing message",
                exception,
                DataMapHolder.getLogMap()
        );
    }

    @Test
    void shouldLogResourceKindOnConsume() {
        // Arrange
        when(message.getPayload()).thenReturn(resourceChangedData);
        when(resourceChangedData.getResourceKind()).thenReturn(RESOURCE_KIND);
        when(resourceChangedData.getContextId()).thenReturn(CONTEXT_ID);

        // Act
        consumer.consume(message);

        // Assert
        verify(logger).info(
                "consume(kind=%s) method called.".formatted(RESOURCE_KIND),
                DataMapHolder.getLogMap()
        );
    }

    @Test
    void shouldLogProcessingTimeOnSuccessfulConsume() {
        // Arrange
        when(message.getPayload()).thenReturn(resourceChangedData);
        when(resourceChangedData.getResourceKind()).thenReturn(RESOURCE_KIND);
        when(resourceChangedData.getContextId()).thenReturn(CONTEXT_ID);

        // Act
        consumer.consume(message);

        // Assert
        verify(logger, times(2)).info(
                anyString(),
                eq(DataMapHolder.getLogMap())
        );
    }
}