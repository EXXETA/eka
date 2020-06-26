package com.exxeta.java.k8s.operator.job;


import static org.mockito.Mockito.*;

import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RootJobTest {

    RootJob rootJob;
    Job child;

    @Before
    public void init(){
        child = new Job() {
            @Override
            public boolean execute() {
                return false;
            }

            @Override
            public String getDescription() {
                return "job";
            }

            @Override
            public Map<String, Object> buildJobTree() {
                return null;
            }

            @Override
            public boolean getSuccessful() {
                return true;
            }

        };
    }

    @Test
    public void dryRunTrueWorks(){
        //given
        Job spy = spy(child);
        rootJob = new RootJob(spy, "true");

        //then
        boolean result = rootJob.execute();
        //verify
        verify(spy, never()).execute();
        Assertions.assertThat(result).isEqualTo(true);

    }

    @Test
    public void dryRunFalseWorks(){
        //given
        //given
        Job spy = spy(child);
        rootJob = new RootJob(spy, "false");

        //then
        boolean result = rootJob.execute();
        //verify
        verify(spy, only()).execute();
        Assertions.assertThat(result).isEqualTo(false);
    }

}