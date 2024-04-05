package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.stream.ResourceChangedData;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.exception.UpsertServiceException;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.util.ServiceParameters;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Component
public class UpsertService implements Service {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpsertService.class);

    // private final String apiUrl;
    // private final String apiKey;
    private final SearchApiClient searchApiClient;


    public UpsertService(SearchApiClient searchApiClient) {
        // this.apiUrl = apiUrl;
        // this.apiKey = apiKey;
        this.searchApiClient = searchApiClient;
    }

    @Override
    public void processMessage(ServiceParameters parameters) {
        ResourceChangedData messageContent = parameters.getData();
        try {
            upsertMessageContent(messageContent);
            System.out.println("message processed");
            LOGGER.info("Successfully processed message: {}", messageContent);
        } catch (UpsertServiceException e) {
            LOGGER.error("Error occurred while processing message: {}", messageContent, e);
        }
    }

    public void upsertMessageContent(ResourceChangedData messageContent) throws UpsertServiceException {
        try {
            CompanyProfileApi companyProfileApi = createCompanyProfileFromMessage(messageContent);
            String number = companyProfileApi.getCompanyNumber();
            String companyNumber = URLEncoder.encode(number, StandardCharsets.UTF_8);
            System.out.println("companyprofile is " + companyProfileApi);
            System.out.println("I got here UpsertMessage");

            searchApiClient.upsertCompanyProfile(companyNumber, companyProfileApi);
            System.out.println("upsert successful");
            LOGGER.info("Successfully upserted message content for company: {}", companyNumber);
        } catch (Exception e) {
            LOGGER.error("Error occurred while upserting message content", e);
            throw new UpsertServiceException("Error occurred while upserting message content", e);
        }
    }

    private CompanyProfileApi createCompanyProfileFromMessage(ResourceChangedData messageContent) {
        CompanyProfileApi companyProfile = new CompanyProfileApi();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(messageContent.getData());
            String companyName = rootNode.get("company_name").toString();
            String companyNumber = rootNode.get("company_number").asText();
            companyProfile.setCompanyNumber(companyNumber);
            companyProfile.setCompanyName(companyName);
            System.out.println("companyProfile created");
        } catch (Exception e) {
            LOGGER.error("Error occurred while creating company profile from message content", e);
        }
        return companyProfile;
    }
}
