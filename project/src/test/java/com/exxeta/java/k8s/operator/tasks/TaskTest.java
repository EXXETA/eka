package com.exxeta.java.k8s.operator.tasks;

import static org.mockito.Mockito.*;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.exxeta.java.k8s.operator.service.ElasticService;

@RunWith(MockitoJUnitRunner.class)
public class TaskTest {

    private Task task;
    private ElasticService elasticServiceMock;

    @Before
    public void init(){
        task = new Task() {
            @Override
            public String getDescription() {
                return "TaskDescription";
            }
        };
        elasticServiceMock = Mockito.mock(ElasticService.class);
        task.elasticService = elasticServiceMock;
    }

    @Test
    public void executeSkipsIfDoneIsTrue(){
        //given
        task.isDone = true;
        Task spyTask = spy(task);
        //then
        boolean result = spyTask.execute();
        //verify
        Assertions.assertThat(result).isEqualTo(true);
        Mockito.verify(spyTask, Mockito.never()).executeIfNotDone();
    }

    @Test
    public void executeWorksIfDoneIsFalse(){
        //given
        task.isDone = false;
        task.method = "GET";
        task.endpoint = "/end/point";
        Task spyTask = spy(task);
        //when
        when(elasticServiceMock.performRequest(any(),any())).thenReturn(true);
        //then
        boolean result = spyTask.execute();
        //verify
        Assertions.assertThat(spyTask.isDone).isEqualTo(result);
        Mockito.verify(spyTask, Mockito.times(1)).executeIfNotDone();

    }


    @Test
    public void buildJobTreeWorksWithNilRequestBody(){
        //given
        task.isDone = false;
        task.requestBody = null;
        //then
        task.buildJobTree();
        //verify

    }

    @Test
    public void buildJobTreeWorksWithProperRequestBody(){
        //given
        task.isDone = true;
        task.requestBody = "{\"request\":\"body\"}";
        //then
        task.buildJobTree();
        //verify
    }

}