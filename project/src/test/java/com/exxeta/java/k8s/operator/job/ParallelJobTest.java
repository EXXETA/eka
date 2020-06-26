package com.exxeta.java.k8s.operator.job;

import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ParallelJobTest {


    private Job subJob_success;
    private Job subJob_fail;

    @Before
    public void init() {

        subJob_success = new Job() {
            @Override
            public boolean execute() {
                return true;
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public Map<String, Object> buildJobTree() {
                return null;
            }

            @Override
            public boolean getSuccessful() {
                return false;
            }

        };
        subJob_success = spy(subJob_success);

        subJob_fail = new Job() {
            @Override
            public boolean execute() {
                return false;
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public Map<String, Object> buildJobTree() {
                return null;
            }

            @Override
            public boolean getSuccessful() {
                return false;
            }

        };
        subJob_fail = spy(subJob_fail);
    }

    @Test
    public void executeWorks(){
        //given
        HashSet set1 = new HashSet();
        set1.add(subJob_success);
        HashSet set2 = new HashSet();
        set2.add(subJob_fail);
        ParallelJob success = new ParallelJob(set1, 1, "job 1", "100");
        ParallelJob fail = new ParallelJob(set2, 1, "job 1", "100");
        //then
        boolean result1 = success.execute();
        boolean result2 = fail.execute();
        //verfiy
        Assertions.assertThat(result1).isEqualTo(true);
        Assertions.assertThat(result2).isEqualTo(false);
    }

    @Test
    public void max_TriesWorks(){
        //given
        int maxTries = 3;
        HashSet set2 = new HashSet();
        set2.add(subJob_fail);
        ParallelJob fail = new ParallelJob(set2, maxTries, "job 1", "100");
        //then
        fail.execute();
        fail.execute();
        //verify
        verify(subJob_fail, times(3)).execute();
    }
}