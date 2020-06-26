package com.exxeta.java.k8s.operator.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.exxeta.java.k8s.operator.job.Job;

@RunWith(MockitoJUnitRunner.class)
public class JobTreeBuilderTest {

    JobTreeBuilder jobTreeBuilder = new JobTreeBuilder();
    JobCollectionBuilder jobCollectionBuilder = new JobCollectionBuilder();

    @Test
    public void buildStringRefJobListWorks(){
        //given
        Job job1 = new Job() {
            @Override
            public boolean execute() {
                return false;
            }

            @Override
            public String getDescription() {
                return "jobA";
            }

            @Override
            public Map<String, Object> buildJobTree() {
                Map<String, Object> map = new HashMap();
                map.put(this.getDescription(), "Cobject");
                return map;
            }

            @Override
            public boolean getSuccessful() {
                return false;
            }

        };
        Job job2 = new Job() {
            @Override
            public boolean execute() {
                return false;
            }

            @Override
            public String getDescription() {
                return "jobB";
            }

            @Override
            public Map<String, Object> buildJobTree() {
                Map<String, Object> map = new HashMap();
                map.put(this.getDescription(), "Bobject");
                return map;
            }

            @Override
            public boolean getSuccessful() {
                return false;
            }

        };
        Job job3 = new Job() {
            @Override
            public boolean execute() {
                return false;
            }

            @Override
            public String getDescription() {
                return "jobC";
            }

            @Override
            public Map<String, Object> buildJobTree() {
                Map<String, Object> map = new HashMap();
                map.put(this.getDescription(), "Aobject");
                return map;
            }

            @Override
            public boolean getSuccessful() {
                return false;
            }

        };

        //then
        Map<String,Object> result1 = jobTreeBuilder.buildStringRefJobList("result", jobCollectionBuilder.build(new ArrayList<Job>(),job3, job2, job1));
        Map<String,Object> result2 = jobTreeBuilder.buildStringRefJobList("result", job3, job2, job1);
        //verify

        Assertions.assertThat((ArrayList) result1.get("result")).containsSequence(job1.buildJobTree(), job2.buildJobTree(), job3.buildJobTree());
        Assertions.assertThat((ArrayList) result2.get("result")).containsSequence(job1.buildJobTree(), job2.buildJobTree(), job3.buildJobTree());

    }

}