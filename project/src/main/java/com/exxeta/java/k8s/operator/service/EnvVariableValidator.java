package com.exxeta.java.k8s.operator.service;


import com.exxeta.java.k8s.operator.config.Environments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Optional;


public class EnvVariableValidator {
    private static Logger LOGGER = LoggerFactory.getLogger(EnvVariableValidator.class);

    public EnumMap<Environments, String> validate(){
        LOGGER.debug("Validating Environments..");
        EnumMap<Environments, String> configMap = new EnumMap<>(Environments.class);
        Arrays.stream(Environments.values()).forEach(
                key -> {
                    String value = System.getenv(key.name());
                    if (!Optional.ofNullable(value).isPresent()) {
                        LOGGER.error("Validating Input failed: Value of " + key + " is null");
                        System.exit(-1);
                    }
                    configMap.put(key, value);
                }
        );
        LOGGER.debug("Validation successful");
        return configMap;
    }
}
