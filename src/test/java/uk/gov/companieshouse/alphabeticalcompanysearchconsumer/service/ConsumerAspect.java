package uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service;

import java.util.concurrent.CountDownLatch;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.logging.Logger;

@Aspect
@Component
public class ConsumerAspect {

    private final CountDownLatch latch;
    private final Logger logger;

    public ConsumerAspect(final CountDownLatch latch, final Logger logger) {
        this.latch = latch;
        this.logger = logger;
    }

    @Before("execution(* uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service.Consumer.consume(..))")
    void beforeConsume(final JoinPoint joinPoint) {
        logger.debug("Consuming Message: Current Latch=%d".formatted(latch.getCount()));
    }

    @After("execution(* uk.gov.companieshouse.alphabeticalcompanysearchconsumer.service.Consumer.consume(..))")
    void afterConsume(final JoinPoint joinPoint) {
        logger.debug("Consumed Message: Current Latch=%d".formatted(latch.getCount()));

        latch.countDown();
    }
}
