package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.util;

import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.exception.NonRetryableException;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.stream.ResourceChangedData;
import java.io.IOException;
import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.logging.LoggingUtils.getRootCause;
import uk.gov.companieshouse.logging.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ApiClientUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static Logger logger = null;

    public static CompanyProfileApi mapMessageToRequest(ServiceParameters parameters) {
        ResourceChangedData data = parameters.getData();
        String jsonData = data.getData();        
        try {
            return objectMapper.readValue(jsonData, CompanyProfileApi.class);
        } catch (IOException e) {
            // Handle the exception appropriately
            final var rootCause = getRootCause(e);
            logger.error(String.format("NonRetryable Error: %s", rootCause));
            throw new NonRetryableException("MessageProcessingUtil.processMessage: ", rootCause);
        }
    }
}
