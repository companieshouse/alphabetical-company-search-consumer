package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.stream.ResourceChangedData;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.exception.UpsertServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class UpsertService {

    private final String upsertUrl;
    private final RestTemplate restTemplate;

    public UpsertService(String upsertUrl, RestTemplate restTemplate) {
        this.upsertUrl = upsertUrl;
        this.restTemplate = new RestTemplate();
    }

   public ResponseEntity<Object> upsertMessageContent(ResourceChangedData messageContent) throws UpsertServiceException {
    try {
        // Extract necessary data from messageContent and create a CompanyProfileApi object
        CompanyProfileApi companyProfile = createCompanyProfileFromMessage(messageContent);
        
        // Extract company number from the created CompanyProfileApi object
        String companyNumber = companyProfile.getCompanyNumber();

        // Construct the URL for the upsert endpoint
        String url = upsertUrl + "/companies/" + companyNumber;
        
        // Send the PUT request to the upsert endpoint with the companyProfile as the request body
        restTemplate.put(url, companyProfile);
        
        // Assuming successful execution, return an empty ResponseEntity with 200 OK status
        return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new UpsertServiceException("Error occurred while upserting message content", e);
        }
    }

    public CompanyProfileApi createCompanyProfileFromMessage(ResourceChangedData messageContent) {
        
        // Extract necessary data from messageContent and create a CompanyProfileApi object
        CompanyProfileApi companyProfile = new CompanyProfileApi();

        try {
            // Step 1: Parse the JSON data within the "data" field
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(messageContent.getData());
            
            // Step 2: Extract company_name and company_number
            String companyName = rootNode.get("company_name").asText();
            String companyNumber = rootNode.get("company_number").asText();
            
            // Step 3: Set the company number and company name in the CompanyProfileApi object
            companyProfile.setCompanyNumber(companyNumber);
            companyProfile.setCompanyName(companyName);

            // You can map other properties similarly
            
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception accordingly
        }

        return companyProfile;
    }
    
}
