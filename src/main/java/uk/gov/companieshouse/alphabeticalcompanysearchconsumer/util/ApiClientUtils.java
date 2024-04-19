package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.util;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.stream.ResourceChangedData;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;


public class ApiClientUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static CompanyProfileApi mapMessageToRequest(ServiceParameters parameters) {
        ResourceChangedData data = parameters.getData();
        String jsonData = data.getData();
        
        try {
            return objectMapper.readValue(jsonData, CompanyProfileApi.class);
        } catch (IOException e) {
            // Handle the exception appropriately
            e.printStackTrace();
            return null; // or throw a custom exception
        }
    }
}
