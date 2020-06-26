package com.exxeta.java.k8s.operator.job;

import static org.mockito.Mockito.*;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SequentialJobTest {

    Queue<Job> jobQueue;
    SequentialJob job;

    @Before
    public void init(){
        jobQueue = new LinkedList<>();

    }

    @Test
    public void executeOnEmptyJobQueue(){
        //given
        job = new SequentialJob(jobQueue, 4, "description", "1000");
        //then
        boolean result = job.execute();
        //verify
        Assertions.assertThat(result).isEqualTo(true);
    }

    @Test
    public void executeSucceeds(){
        //given
        Job subJob1 = mock(Job.class);
        Job subJob2 = mock(Job.class);
        jobQueue.add(subJob1);
        jobQueue.add(subJob2);
        job = new SequentialJob(jobQueue, 2, "description", "1000");
        //when
        when(subJob1.execute()).thenReturn(true);
        when(subJob2.execute()).thenReturn(true);
        //then
        boolean result = job.execute();
        //verify
        Assertions.assertThat(result).isEqualTo(true);
    }
    @Test
    public void executeSucceedsWithNullJob(){
        //given
        jobQueue.add(null);
        job = new SequentialJob(jobQueue, 2, "description", "1000");
        //then
        boolean result = job.execute();
        //verify
        Assertions.assertThat(result).isEqualTo(true);
    }
    @Test
    public void executeFails(){
        //given
        Job subJob1 = mock(Job.class);
        Job subJob2 = mock(Job.class);
        jobQueue.add(subJob1);
        jobQueue.add(subJob2);

        job = new SequentialJob(jobQueue, 4, "description", "1000");
        //when
        when(subJob1.execute()).thenReturn(true);
        when(subJob2.execute()).thenReturn(false);
        //then
        boolean result = job.execute();
        //verify
        Assertions.assertThat(result).isEqualTo(false);
    }
}