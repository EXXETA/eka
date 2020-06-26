package com.exxeta.java.k8s.operator.service;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.exxeta.java.k8s.operator.Pojo.Beat;
import com.exxeta.java.k8s.operator.Pojo.Beats;
import com.exxeta.java.k8s.operator.Pojo.IlmPolicy;
import com.exxeta.java.k8s.operator.utility.SystemWrapper;

@RunWith(MockitoJUnitRunner.class)
public class BeatValidatorTest {

    BeatValidator beatValidator;
    Beat beat;
    Beats beats;
    IlmPolicy ilmPolicy;
    SystemWrapper systemWrapperMock;

    @Before
    public void init(){
        beatValidator = new BeatValidator();

        systemWrapperMock = mock(SystemWrapper.class);
        beatValidator.setSystemWrapper(systemWrapperMock);

        beats = new Beats();
        ilmPolicy = new IlmPolicy();
        ilmPolicy.setRequestBody("{somthing}");
        ilmPolicy.setName("ilmPolicy");

        beat = new Beat();
        beat.setBeatName("beat");
        beat.setIndexSuffix("be-suff");
        beat.setVersion("7.1");
        beat.setFallbackIndexName("fallback");
        beat.setIlmPolicy(ilmPolicy);

        beats.setBeats(new ArrayList<>());
        beats.getBeats().add(beat);
    }

    @Test
    public void validateWorks(){
        //then
        boolean result = beatValidator.validate(beats);
        //verify
        Assertions.assertThat(result).isEqualTo(true);
    }

    @Test
    public void validateFailWithNullValueInBeat(){
        //given
        beat.setVersion(null);
        //then
        boolean result = beatValidator.validate(beats);
        //verify
        Assertions.assertThat(result).isEqualTo(false);
    }

    @Test
    public void validateFailWithNullValueInIlmPolicy(){
        //given
        ilmPolicy.setName(null);
        //then
        boolean result = beatValidator.validate(beats);
        //verify
        Assertions.assertThat(result).isEqualTo(false);
    }

    @Test
    public void setIlmPolicyRequestBodyFailsAtGettingRequestBodyFromEnv(){
        //then
        boolean result = beatValidator.setIlmPolicyRequestBody(beats);
        //verify
        Assertions.assertThat(result).isEqualTo(false);
    }
    @Test
    public void setIlmPolicyRequestBodySucceeds(){
        //when
        when(systemWrapperMock.getenv(any())).thenReturn("RequestBodyStringJson");
        //then
        boolean result = beatValidator.setIlmPolicyRequestBody(beats);
        //verify
        Assertions.assertThat(result).isEqualTo(true);
    }
}