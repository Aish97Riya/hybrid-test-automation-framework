package com.framework.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * RetryAnalyzer - Retries failed tests up to MAX_RETRY_COUNT times.
 *
 * <p>SOLID: Single Responsibility - only handles retry logic.</p>
 *
 * Usage: Annotate test methods with:
 * <pre>{@code @Test(retryAnalyzer = RetryAnalyzer.class)}</pre>
 * Or configure globally via {@link RetryTransformer}.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger log = LogManager.getLogger(RetryAnalyzer.class);

    private static final int MAX_RETRY_COUNT = 3;
    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY_COUNT) {
            retryCount++;
            log.warn("Retrying test [{}] - Attempt {}/{} | Failure: {}",
                    result.getMethod().getMethodName(),
                    retryCount,
                    MAX_RETRY_COUNT,
                    result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown");
            return true;
        }
        log.error("Test [{}] EXHAUSTED all {} retry attempts. Marking as FAILED.",
                result.getMethod().getMethodName(), MAX_RETRY_COUNT);
        return false;
    }
}
