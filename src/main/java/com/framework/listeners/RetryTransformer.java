package com.framework.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * RetryTransformer - IAnnotationTransformer that globally applies RetryAnalyzer
 * to every @Test method without requiring per-test annotation.
 *
 * <p>Register in testng.xml:</p>
 * <pre>{@code
 * <listeners>
 *   <listener class-name="com.framework.listeners.RetryTransformer"/>
 * </listeners>
 * }</pre>
 *
 * <p>SOLID: Open/Closed - applies retry without modifying any test class.</p>
 */
public class RetryTransformer implements IAnnotationTransformer {

    private static final Logger log = LogManager.getLogger(RetryTransformer.class);

    @Override
    public void transform(ITestAnnotation annotation,
                          Class testClass,
                          Constructor testConstructor,
                          Method testMethod) {
        if (annotation.getRetryAnalyzerClass() == null) {
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
            if (testMethod != null) {
                log.debug("RetryAnalyzer applied to: {}.{}",
                        testMethod.getDeclaringClass().getSimpleName(),
                        testMethod.getName());
            }
        }
    }
}
