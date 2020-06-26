package com.exxeta.java.k8s.operator.service;

import com.exxeta.java.k8s.operator.Pojo.Beat;
import com.exxeta.java.k8s.operator.Pojo.Beats;
import com.exxeta.java.k8s.operator.Pojo.IlmPolicy;
import com.exxeta.java.k8s.operator.utility.SystemWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class BeatValidator {
    private static Logger LOGGER = LoggerFactory.getLogger(BeatValidator.class);
    private SystemWrapper systemWrapper = new SystemWrapper();

    public BeatValidator() {
    }

    public boolean validate(Beats beats) {
        LOGGER.info("Validating imported agents ..");
        for (Beat beat : beats.getBeats()) {
            if (beat.hasNullValues()) {
                LOGGER.error("beat has null values \n" + beat.toString());
                return false;
            } else {
                IlmPolicy policy = beat.getIlmPolicy();
                if (policy.hasNullValues()) {
                    LOGGER.error("policy has null values\n" + policy.toString());
                    return false;
                }
            }
        }
        return true;
    }

    public boolean setIlmPolicyRequestBody(Beats beats) {
        for (Beat beat : beats.getBeats()) {
            String requestBody = systemWrapper.getenv(beat.getIlmPolicy().getRequestBody());
            if(Objects.nonNull(requestBody)) {
                beat.getIlmPolicy().setRequestBody(requestBody);
            } else {
                LOGGER.error("IlmPolicy request body {} of beat {} is not defined as environment variable, check" +
                        "the configmap configuration again", beat.getBeatName(), beat.getIlmPolicy().getRequestBody());
                return false;
            }
        }
        return true;
    }

    void setSystemWrapper(SystemWrapper systemWrapper) {
        this.systemWrapper = systemWrapper;
    }
}
