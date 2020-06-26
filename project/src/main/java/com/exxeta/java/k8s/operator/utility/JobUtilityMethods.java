package com.exxeta.java.k8s.operator.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobUtilityMethods {
    protected static Logger LOGGER = LoggerFactory.getLogger(JobUtilityMethods.class);

    public void sleep(long sleepTimer, int tries) {
        LOGGER.info("Job execution Try: {}", tries);
        try {
            Thread.sleep(sleepTimer);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public long getSleepTimer(String sleepTimer)
    {
        long defaultSleepTimer = 5000;
        try {
            return Long.parseLong(sleepTimer);

        } catch (NumberFormatException e) {
            LOGGER.error("SLEEP_TIMER is not correctly formatted. Defaulting back to {}", defaultSleepTimer);
            return defaultSleepTimer;
        }
    }
}
