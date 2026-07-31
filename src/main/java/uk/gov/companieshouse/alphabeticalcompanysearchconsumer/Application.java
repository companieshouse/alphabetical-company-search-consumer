package uk.gov.companieshouse.alphabeticalcompanysearchconsumer;

import static org.springframework.boot.SpringApplication.run;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static final String NAMESPACE = "alphabetical-company-search-consumer";

    public static void main(String[] args) {
        run(Application.class, args);
    }

}
