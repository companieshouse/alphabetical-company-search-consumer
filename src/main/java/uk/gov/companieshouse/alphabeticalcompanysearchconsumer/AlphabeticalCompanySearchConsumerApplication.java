package uk.gov.companieshouse.alphabeticalcompanysearchconsumer;

import static org.springframework.boot.SpringApplication.run;
import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.environment.EnvironmentVariablesChecker.allRequiredEnvironmentVariablesPresent;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AlphabeticalCompanySearchConsumerApplication {

    public static final String NAMESPACE = "alphabetical-company-search-consumer";

    public static void main(String[] args) {
        if (allRequiredEnvironmentVariablesPresent()) {
            run(AlphabeticalCompanySearchConsumerApplication.class, args);
        }
    }

}
