package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.util;

import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.Application.NAMESPACE;
import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.logging.LoggingUtils.getRootCause;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.exception.NonRetryableException;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.stream.ResourceChangedData;


public class ApiClientUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);

    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static CompanyProfileApi mapMessageToRequest(ServiceParameters parameters) {
        ResourceChangedData data = parameters.getData();
        String jsonData = data.getData();        
        try {
            return objectMapper.readValue(jsonData, CompanyProfileApi.class);

        } catch (IOException e) {
            // Handle the exception appropriately
            final var rootCause = getRootCause(e);
            LOGGER.error(String.format("NonRetryable Error: %s", rootCause));
            throw new NonRetryableException("ApiClientUtils.processMessage: ", rootCause);
        }
    }
}
