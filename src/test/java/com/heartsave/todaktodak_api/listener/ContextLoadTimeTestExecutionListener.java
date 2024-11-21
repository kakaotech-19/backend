package com.heartsave.todaktodak_api.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

@Slf4j
public class ContextLoadTimeTestExecutionListener implements TestExecutionListener {
    private long startTime;

    @Override
    public void beforeTestClass(TestContext testContext) {
        startTime = System.currentTimeMillis();
        log.info("Starting to load ApplicationContext for test class: {}",
                testContext.getTestClass().getSimpleName());
    }

    @Override
    public void afterTestClass(TestContext testContext) {
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        log.info("ApplicationContext load time for test class {}: {}ms",
                testContext.getTestClass().getSimpleName(), totalTime);
    }
}