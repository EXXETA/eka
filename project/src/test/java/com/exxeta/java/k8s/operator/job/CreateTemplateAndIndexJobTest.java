package com.exxeta.java.k8s.operator.job;

import static org.mockito.Mockito.*;

import org.apache.http.HttpStatus;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.exxeta.java.k8s.operator.Pojo.Beat;
import com.exxeta.java.k8s.operator.tasks.Index.GetAliasTask;
import com.exxeta.java.k8s.operator.tasks.Index.PutIndexTask;
import com.exxeta.java.k8s.operator.tasks.Index.PutIndexTemplateTask;

@RunWith(MockitoJUnitRunner.class)
public class CreateTemplateAndIndexJobTest {

    GetAliasTask getRollOverMock;
    PutIndexTemplateTask putIndexTemplateMock;
    PutIndexTask putIndexMock;
    Beat beat;
    CreateTemplateAndIndexJob job;

    @Before
    public void init(){
        getRollOverMock = mock(GetAliasTask.class);
        putIndexTemplateMock = mock(PutIndexTemplateTask.class);
        putIndexMock = mock(PutIndexTask.class);
        beat = new Beat();
        job = spy(new CreateTemplateAndIndexJob(getRollOverMock, putIndexTemplateMock, putIndexMock, beat, "description"));
    }


    @Test
    public void executeFailsWithUninitializedBeat(){
        //given
        beat.setInitialized(false);
        //then
        boolean result = job.execute();
        //verify
        Assertions.assertThat(result).isEqualTo(false);
        verify(getRollOverMock, never()).execute();
        verify(putIndexTemplateMock, never()).execute();
        verify(putIndexMock, never()).execute();
    }

    @Test
    public void executeSucceedsWithExistingRolloverAlias(){
        //given
        beat.setInitialized(true);
        when(getRollOverMock.execute()).thenReturn(true);
        when(getRollOverMock.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        //then
        boolean result = job.execute();
        //verify
        Assertions.assertThat(result).isEqualTo(true);
    }

    @Test
    public void executeSucceedsWithMissingRolloverAlias(){
        //given
        beat.setInitialized(true);
        when(getRollOverMock.execute()).thenReturn(true);
        when(putIndexTemplateMock.execute()).thenReturn(true);
        when(putIndexMock.execute()).thenReturn(true);
        when(getRollOverMock.getStatusCode()).thenReturn(HttpStatus.SC_NOT_FOUND);
        when(putIndexTemplateMock.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        when(putIndexMock.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        //then
        boolean result = job.execute();
        //verify
        Assertions.assertThat(result).isEqualTo(true);
    }

    @Test
    public void executeFailsWithNoResponseGetRolloverAlias(){
        //given
        beat.setInitialized(true);
        when(getRollOverMock.execute()).thenReturn(false);
        //then
        boolean result = job.execute();
        //verify
        verify(putIndexMock,never()).execute();
        verify(putIndexTemplateMock, never()).execute();
        Assertions.assertThat(result).isEqualTo(false);
    }

    @Test
    public void executeFailsWithNoResponsePutIndexTemplate(){
        //given
        beat.setInitialized(true);
        when(getRollOverMock.execute()).thenReturn(true);
        when(putIndexTemplateMock.execute()).thenReturn(false);
        when(getRollOverMock.getStatusCode()).thenReturn(HttpStatus.SC_NOT_FOUND);
        //then
        boolean result = job.execute();
        //verify
        Assertions.assertThat(result).isEqualTo(false);
        verify(putIndexMock, never()).execute();
    }

    @Test
    public void executeFailsWithNoResponsePutIndex(){
        //given
        beat.setInitialized(true);
        when(getRollOverMock.execute()).thenReturn(true);
        when(putIndexTemplateMock.execute()).thenReturn(true);
        when(putIndexMock.execute()).thenReturn(false);

        when(getRollOverMock.getStatusCode()).thenReturn(HttpStatus.SC_NOT_FOUND);
        when(putIndexTemplateMock.getStatusCode()).thenReturn(HttpStatus.SC_OK);

        //then
        boolean result = job.execute();
        //verify
        Assertions.assertThat(result).isEqualTo(false);
    }

    @Test
    public void executeFailsWithUnknownStatusCodeGetRolloverAlias(){
        //given
        beat.setInitialized(true);
        when(getRollOverMock.execute()).thenReturn(true);
        when(getRollOverMock.getStatusCode()).thenReturn(-1);
        //then
        boolean result = job.execute();
        //verify
        Assertions.assertThat(result).isEqualTo(false);
    }
    @Test
    public void executeFailsWithUnknownStatusCodePutIndexTemplate(){
        //given
        beat.setInitialized(true);
        when(getRollOverMock.execute()).thenReturn(true);
        when(putIndexTemplateMock.execute()).thenReturn(true);

        when(getRollOverMock.getStatusCode()).thenReturn(HttpStatus.SC_NOT_FOUND);
        when(putIndexTemplateMock.getStatusCode()).thenReturn(-1);
        //then
        boolean result = job.execute();
        //verify
        Assertions.assertThat(result).isEqualTo(false);
    }

    @Test
    public void executeFailsWithUnknownStatusCodePutIndex(){
        //given
        beat.setInitialized(true);
        when(getRollOverMock.execute()).thenReturn(true);
        when(putIndexTemplateMock.execute()).thenReturn(true);
        when(putIndexMock.execute()).thenReturn(true);

        when(getRollOverMock.getStatusCode()).thenReturn(HttpStatus.SC_NOT_FOUND);
        when(putIndexTemplateMock.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        when(putIndexMock.getStatusCode()).thenReturn(-1);
        //then
        boolean result = job.execute();
        //verify
        Assertions.assertThat(result).isEqualTo(false);
    }

}
