package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.environment;

import java.util.List;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.logging.Logger;

@Component
public class EnvironmentValidator implements EnvironmentPostProcessor {

    private static final List<String> REQUIRED_VARIABLES = List.of(
            "BACKOFF_DELAY",
            "BOOTSTRAP_SERVER_URL",
            "CONCURRENT_LISTENER_INSTANCES",
            "GROUP_ID",
            "MAX_ATTEMPTS",
            "SERVER_PORT",
            "TOPIC"
    );

    private final Logger logger;

    public EnvironmentValidator(final Logger logger) {
        this.logger = logger;
    }

    @Override
    public void postProcessEnvironment(@NonNull ConfigurableEnvironment environment, @NonNull SpringApplication application) {
        logger.info("Checking required environment variables are present...");

        List<String> missingVariables = REQUIRED_VARIABLES.stream()
                .filter(variable -> !environment.containsProperty(variable))
                .toList();

        if (!missingVariables.isEmpty()) {
            throw new IllegalStateException("Missing required environment variables: %s".formatted(missingVariables));
        }

    }
}
