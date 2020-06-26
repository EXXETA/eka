package com.exxeta.java.k8s.operator.service;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EnvVariableValidatorTest {

    EnvVariableValidator envVariableValidator;

    @Before
    public void init(){
        envVariableValidator = new EnvVariableValidator();
    }

    @Ignore
    @Test
    public void validateExitsOnNull(){
        //Then
        envVariableValidator.validate();
    }
}