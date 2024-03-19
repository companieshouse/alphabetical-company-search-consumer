package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.exception.UpsertServiceException;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.util.ServiceParameters;
import uk.gov.companieshouse.stream.ResourceChangedData;
import uk.gov.companieshouse.logging.util.DataMap;

@Service
public class UpsertService {

    @Value("${search.api.base.url}")
    private String searchApiBaseUrl;

    private final RestTemplate restTemplate;

    public UpsertService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void upsertCompanySearchEntity(ServiceParameters parameters) {
        ResourceChangedData payload = parameters.getData();
        String companyNumber = payload.getResourceId();
        String endpointUrl = "/alphabetical-search/companies/" + companyNumber;

        HttpHeaders headers = new HttpHeaders();

        // Map the payload data to company search entity
         DataMap companySearchEntity = mapToDataMap(payload.getData());

        HttpEntity<?> requestEntity = new HttpEntity<>(companySearchEntity, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    endpointUrl,
                    HttpMethod.PUT,
                    requestEntity,
                    String.class
            );
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new UpsertServiceException("Failed to upsert company search entity: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new UpsertServiceException("Error while upserting company search entity", e);
        }
    }

    private DataMap mapToDataMap(String companyNumber) {
        // Construct a DataMap using the builder pattern
        DataMap.Builder builder = new DataMap.Builder();
        builder.companyNumber(companyNumber); // Set the company number
        DataMap dataMap = builder.build();
        return dataMap;
    }
}
