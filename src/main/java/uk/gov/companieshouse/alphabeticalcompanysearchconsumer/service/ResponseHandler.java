package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service;
import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.AlphabeticalCompanySearchConsumerApplication.NAMESPACE;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.exception.NonRetryableException;
import uk.gov.companieshouse.alphabeticalcompanysearchconsumer.exception.RetryableException;
import static uk.gov.companieshouse.alphabeticalcompanysearchconsumer.logging.LoggingUtils.getLogMap;


@Component
public class ResponseHandler {

    private static final Logger logger = LoggerFactory.getLogger(NAMESPACE);

    public void handle(String message, URIValidationException ex) {
        logger.error(message, ex, getLogMap(null));
        throw new NonRetryableException(message, ex);
    }

    public void handle(String message, IllegalArgumentException ex) {
        String causeMessage = ex.getCause() != null
                ? String.format("; %s", ex.getCause().getMessage()) : "";
                logger.info(message + causeMessage, getLogMap(null));
        throw new RetryableException(message, ex);
    }

    public void handle(String message, ApiErrorResponseException ex) {
        if (HttpStatus.valueOf(ex.getStatusCode()).is5xxServerError()) {
            logger.info(message, getLogMap(null));
            throw new RetryableException(message, ex);
        } else {
            logger.error(message, ex, getLogMap(null));
            throw new NonRetryableException(message, ex);
        }
    }
}
