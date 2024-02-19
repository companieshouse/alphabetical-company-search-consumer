package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service;

import static org.apache.commons.io.IOUtils.resourceToString;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.utils.TestConstants.UPDATE;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.util.ServiceParameters;
import uk.gov.companieshouse.logging.Logger;

@ExtendWith(MockitoExtension.class)
class AlphabeticalIndexUpdaterServiceTest {

    @InjectMocks
    private AlphabeticalIndexUpdaterService alphabeticalIndexUpdaterService;

    @Mock
    private Logger logger;

    @Mock
    private ServiceParameters serviceParameters;

    @Test
    @DisplayName("processMessage() logs message clearly")
    void processMessageLogsMessageClearly() throws IOException {

        // Given
        when(serviceParameters.getData()).thenReturn(UPDATE);

        // When
        alphabeticalIndexUpdaterService.processMessage(serviceParameters);

        // Then
        final var expectedLogMessage = resourceToString("/fixtures/expected-log-message.txt",
            StandardCharsets.UTF_8);
        verify(logger).info(eq(expectedLogMessage), anyMap());

    }

}