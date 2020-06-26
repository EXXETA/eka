package com.exxeta.java.k8s.operator.service;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.assertj.core.api.Assertions;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.exxeta.java.k8s.operator.tasks.Task;

@RunWith(MockitoJUnitRunner.class)
public class ElasticServiceTest {

    private ElasticService elasticService;
    private Task task;
    private RestClient restClientMock;
    private LoggerService loggerServiceMock;
    @Before
    public void init(){
        restClientMock = mock(RestClient.class);
        loggerServiceMock = mock(LoggerService.class);
        elasticService = new ElasticService(restClientMock, loggerServiceMock);
        task = new Task() {
            @Override
            public String getDescription() {
                return "taskDescription";
            }
        };
    }

    @Test
    public void performRequestWorks() throws IOException {
        //given
        Response response = mock(Response.class);
        Request request =  new Request("GET", "/end/point");

        //when
        when(restClientMock.performRequest(any())).thenReturn(response);
        when(loggerServiceMock.readResponseContent(any())).thenReturn("response");
        //then
        boolean result = elasticService.performRequest(request, task);
        //verify
        Assertions.assertThat(response).isEqualTo(task.getResponse());
        Assertions.assertThat(result).isEqualTo(true);
    }

    //unfinished
    public void performRequestWithResponseException() throws IOException {
        Response response = mock(Response.class);
        Request request =  new Request("GET", "/end/point");
        ElasticService spyElasticService = spy(elasticService);
        ResponseException responseException = new ResponseException(response);

        //when
        doThrow(responseException).when(spyElasticService.performRequest(any(),any()));
        when(loggerServiceMock.readResponseContent(any())).thenReturn("response");
        when(spyElasticService.responseExceptionAction(any())).thenReturn(false);

        //then

        boolean result = spyElasticService.performRequest(request, task);
        //verify
        verify(spyElasticService.responseExceptionAction(any()),times(1));
        Assertions.assertThat(result).isEqualTo(true);
    }



}