package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.environment;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import uk.gov.companieshouse.logging.Logger;

@ExtendWith(MockitoExtension.class)
class EnvironmentValidatorTest {

    @Mock
    private Logger logger;

    @Mock
    private ConfigurableEnvironment environment;

    @Mock
    private SpringApplication application;

    @InjectMocks
    private EnvironmentValidator validator;

    @BeforeEach
    void setUp() throws Exception{
        MockitoAnnotations.openMocks(this).close();
    }

    @Test
    void shouldStartSuccessfullyWhenAllVariablesPresent() {
        // Arrange
        when(environment.containsProperty(anyString())).thenReturn(true);

        // Act & Assert
        assertDoesNotThrow(() -> validator.postProcessEnvironment(environment, application));
    }

    @Test
    void shouldThrowExceptionWhenBackoffDelayIsMissing() {
        // Arrange
        when(environment.containsProperty("BACKOFF_DELAY")).thenReturn(false);
        when(environment.containsProperty("BOOTSTRAP_SERVER_URL")).thenReturn(true);
        when(environment.containsProperty("CONCURRENT_LISTENER_INSTANCES")).thenReturn(true);
        when(environment.containsProperty("GROUP_ID")).thenReturn(true);
        when(environment.containsProperty("MAX_ATTEMPTS")).thenReturn(true);
        when(environment.containsProperty("SERVER_PORT")).thenReturn(true);
        when(environment.containsProperty("TOPIC")).thenReturn(true);

        // Act
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> validator.postProcessEnvironment(environment, application));

        // Assert
        assertTrue(exception.getMessage().contains("BACKOFF_DELAY"));
        assertFalse(exception.getMessage().contains("BOOTSTRAP_SERVER_URL"));
    }

    @Test
    void shouldThrowExceptionWhenBootstrapServerUrlIsMissing() {
        // Arrange
        when(environment.containsProperty("BACKOFF_DELAY")).thenReturn(true);
        when(environment.containsProperty("BOOTSTRAP_SERVER_URL")).thenReturn(false);
        when(environment.containsProperty("CONCURRENT_LISTENER_INSTANCES")).thenReturn(true);
        when(environment.containsProperty("GROUP_ID")).thenReturn(true);
        when(environment.containsProperty("MAX_ATTEMPTS")).thenReturn(true);
        when(environment.containsProperty("SERVER_PORT")).thenReturn(true);
        when(environment.containsProperty("TOPIC")).thenReturn(true);

        // Act
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> validator.postProcessEnvironment(environment, application));

        // Assert
        assertTrue(exception.getMessage().contains("BOOTSTRAP_SERVER_URL"));
        assertFalse(exception.getMessage().contains("BACKOFF_DELAY"));
    }

    @Test
    void shouldThrowExceptionWhenConcurrentListenerInstancesIsMissing() {
        // Arrange
        when(environment.containsProperty("BACKOFF_DELAY")).thenReturn(true);
        when(environment.containsProperty("BOOTSTRAP_SERVER_URL")).thenReturn(true);
        when(environment.containsProperty("CONCURRENT_LISTENER_INSTANCES")).thenReturn(false);
        when(environment.containsProperty("GROUP_ID")).thenReturn(true);
        when(environment.containsProperty("MAX_ATTEMPTS")).thenReturn(true);
        when(environment.containsProperty("SERVER_PORT")).thenReturn(true);
        when(environment.containsProperty("TOPIC")).thenReturn(true);

        // Act
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> validator.postProcessEnvironment(environment, application));

        // Assert
        assertTrue(exception.getMessage().contains("CONCURRENT_LISTENER_INSTANCES"));
        assertFalse(exception.getMessage().contains("BACKOFF_DELAY"));
    }

    @Test
    void shouldThrowExceptionWhenGroupIdIsMissing() {
        // Arrange
        when(environment.containsProperty("BACKOFF_DELAY")).thenReturn(true);
        when(environment.containsProperty("BOOTSTRAP_SERVER_URL")).thenReturn(true);
        when(environment.containsProperty("CONCURRENT_LISTENER_INSTANCES")).thenReturn(true);
        when(environment.containsProperty("GROUP_ID")).thenReturn(false);
        when(environment.containsProperty("MAX_ATTEMPTS")).thenReturn(true);
        when(environment.containsProperty("SERVER_PORT")).thenReturn(true);
        when(environment.containsProperty("TOPIC")).thenReturn(true);

        // Act
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> validator.postProcessEnvironment(environment, application));

        // Assert
        assertTrue(exception.getMessage().contains("GROUP_ID"));
        assertFalse(exception.getMessage().contains("BACKOFF_DELAY"));
    }

    @Test
    void shouldThrowExceptionWhenMaxAttemptsIsMissing() {
        // Arrange
        when(environment.containsProperty("BACKOFF_DELAY")).thenReturn(true);
        when(environment.containsProperty("BOOTSTRAP_SERVER_URL")).thenReturn(true);
        when(environment.containsProperty("CONCURRENT_LISTENER_INSTANCES")).thenReturn(true);
        when(environment.containsProperty("GROUP_ID")).thenReturn(true);
        when(environment.containsProperty("MAX_ATTEMPTS")).thenReturn(false);
        when(environment.containsProperty("SERVER_PORT")).thenReturn(true);
        when(environment.containsProperty("TOPIC")).thenReturn(true);

        // Act
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> validator.postProcessEnvironment(environment, application));

        // Assert
        assertTrue(exception.getMessage().contains("MAX_ATTEMPTS"));
        assertFalse(exception.getMessage().contains("BACKOFF_DELAY"));
    }

    @Test
    void shouldThrowExceptionWhenServerPortIsMissing() {
        // Arrange
        when(environment.containsProperty("BACKOFF_DELAY")).thenReturn(true);
        when(environment.containsProperty("BOOTSTRAP_SERVER_URL")).thenReturn(true);
        when(environment.containsProperty("CONCURRENT_LISTENER_INSTANCES")).thenReturn(true);
        when(environment.containsProperty("GROUP_ID")).thenReturn(true);
        when(environment.containsProperty("MAX_ATTEMPTS")).thenReturn(true);
        when(environment.containsProperty("SERVER_PORT")).thenReturn(false);
        when(environment.containsProperty("TOPIC")).thenReturn(true);

        // Act
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> validator.postProcessEnvironment(environment, application));

        // Assert
        assertTrue(exception.getMessage().contains("SERVER_PORT"));
        assertFalse(exception.getMessage().contains("BACKOFF_DELAY"));
    }

    @Test
    void shouldThrowExceptionWhenTopicIsMissing() {
        // Arrange
        when(environment.containsProperty("BACKOFF_DELAY")).thenReturn(true);
        when(environment.containsProperty("BOOTSTRAP_SERVER_URL")).thenReturn(true);
        when(environment.containsProperty("CONCURRENT_LISTENER_INSTANCES")).thenReturn(true);
        when(environment.containsProperty("GROUP_ID")).thenReturn(true);
        when(environment.containsProperty("MAX_ATTEMPTS")).thenReturn(true);
        when(environment.containsProperty("SERVER_PORT")).thenReturn(true);
        when(environment.containsProperty("TOPIC")).thenReturn(false);

        // Act
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> validator.postProcessEnvironment(environment, application));

        // Assert
        assertTrue(exception.getMessage().contains("TOPIC"));
        assertFalse(exception.getMessage().contains("BACKOFF_DELAY"));
    }

    @Test
    void shouldThrowExceptionWhenAllVariablesAreMissing() {
        // Arrange
        when(environment.containsProperty(anyString())).thenReturn(false);

        // Act
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> validator.postProcessEnvironment(environment, application));

        // Assert
        assertTrue(exception.getMessage().contains("BACKOFF_DELAY"));
        assertTrue(exception.getMessage().contains("BOOTSTRAP_SERVER_URL"));
        assertTrue(exception.getMessage().contains("CONCURRENT_LISTENER_INSTANCES"));
        assertTrue(exception.getMessage().contains("GROUP_ID"));
        assertTrue(exception.getMessage().contains("MAX_ATTEMPTS"));
        assertTrue(exception.getMessage().contains("SERVER_PORT"));
        assertTrue(exception.getMessage().contains("TOPIC"));
    }

    @Test
    void shouldThrowExceptionWhenMultipleVariablesAreMissing() {
        // Arrange
        when(environment.containsProperty("BACKOFF_DELAY")).thenReturn(false);
        when(environment.containsProperty("BOOTSTRAP_SERVER_URL")).thenReturn(true);
        when(environment.containsProperty("CONCURRENT_LISTENER_INSTANCES")).thenReturn(false);
        when(environment.containsProperty("GROUP_ID")).thenReturn(true);
        when(environment.containsProperty("MAX_ATTEMPTS")).thenReturn(false);
        when(environment.containsProperty("SERVER_PORT")).thenReturn(true);
        when(environment.containsProperty("TOPIC")).thenReturn(true);

        // Act
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> validator.postProcessEnvironment(environment, application));

        // Assert
        assertTrue(exception.getMessage().contains("BACKOFF_DELAY"));
        assertTrue(exception.getMessage().contains("CONCURRENT_LISTENER_INSTANCES"));
        assertTrue(exception.getMessage().contains("MAX_ATTEMPTS"));
        assertFalse(exception.getMessage().contains("BOOTSTRAP_SERVER_URL"));
        assertFalse(exception.getMessage().contains("GROUP_ID"));
    }

    @Test
    void shouldIncludeMissingVariablesInExceptionMessage() {
        // Arrange
        when(environment.containsProperty(anyString())).thenReturn(false);

        // Act
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> validator.postProcessEnvironment(environment, application));

        // Assert
        assertNotNull(exception.getMessage());
        assertFalse(exception.getMessage().isEmpty());
        assertTrue(exception.getMessage().startsWith("Missing required environment variables:"));
    }
}