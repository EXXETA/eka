package com.exxeta.java.k8s.operator.builder;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.exxeta.java.k8s.operator.Pojo.Beat;
import com.exxeta.java.k8s.operator.Pojo.NamespaceData;

@RunWith(MockitoJUnitRunner.class)
public class NamespaceDataBuilderTest {

    private NamespaceNamesBuilder builder;
    private Beat beat;
    private String nameSpaceName = "namespace";

    @Before
    public void init() {
        builder = new NamespaceNamesBuilder();
        beat = new Beat();
        beat.setVersion("7.5.0");
        beat.setBeatName("testbeat");
        beat.setIndexSuffix("test");
    }


    @Test
    public void buildIndexName_works() {
        //given
        String expected = "test-123-k8-0001";
        //then
        String actual = builder.buildName("test", "123", "k8", "0001");
        //verify
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void buildForFallbackIndexWorks(){
        //given
        String expectedIndexName            = "testbeat-k8s-7.5.0-000001";
        String expectedAliasName            = "testbeat-k8s-7.5.0-alias";
        String expectedRolloverAliasName    = "testbeat-k8s-7.5.0-rollover";
        String expectedTemplateName         = "testbeat-k8s-7.5.0-template";
        String expectedIndexPattern         = "testbeat-k8s-7.5.0-*";
        //then
        NamespaceData data = builder.buildForJobFallbackIndex(beat);
        //verify
        Assertions.assertThat(data.getIndexName()).isEqualTo(expectedIndexName);
        Assertions.assertThat(data.getAliasName()).isEqualTo(expectedAliasName);
        Assertions.assertThat(data.getRolloverAliasName()).isEqualTo(expectedRolloverAliasName);
        Assertions.assertThat(data.getTemplateName()).isEqualTo(expectedTemplateName);
        Assertions.assertThat(data.getIndexPattern()).isEqualTo(expectedIndexPattern);
    }

    @Test
    public void buildForJobWithIndexAlias(){
        //given
        String expectedIndexName            = "namespace-test-7.5.0-000001";
        String expectedAliasName            = "namespace-test-7.5.0-alias";
        String expectedRolloverAliasName    = "namespace-test-7.5.0-rollover";
        String expectedTemplateName         = "namespace-test-7.5.0-template";
        String expectedIndexPattern         = "namespace-test-7.5.0-*";
        //then
        NamespaceData data = builder.buildForJobWithIndexAlias(nameSpaceName,beat);
        //verify
        Assertions.assertThat(data.getIndexName()).isEqualTo(expectedIndexName);
        Assertions.assertThat(data.getAliasName()).isEqualTo(expectedAliasName);
        Assertions.assertThat(data.getRolloverAliasName()).isEqualTo(expectedRolloverAliasName);
        Assertions.assertThat(data.getTemplateName()).isEqualTo(expectedTemplateName);
        Assertions.assertThat(data.getIndexPattern()).isEqualTo(expectedIndexPattern);
    }

    @Test
    public void buildForJob(){
        //given
        String expectedIndexName            = "namespace-k8s-test-7.5.0-000001";
        String expectedAliasName            = "namespace-k8s-test-7.5.0-alias";
        String expectedRolloverAliasName    = "namespace-k8s-test-7.5.0-rollover";
        String expectedTemplateName         = "namespace-k8s-test-7.5.0-template";
        String expectedIndexPattern         = "namespace-k8s-test-7.5.0-*";
        //then
        NamespaceData data = builder.buildForJob(nameSpaceName, beat);
        //verify
        Assertions.assertThat(data.getIndexName()).isEqualTo(expectedIndexName);
        Assertions.assertThat(data.getAliasName()).isEqualTo(expectedAliasName);
        Assertions.assertThat(data.getRolloverAliasName()).isEqualTo(expectedRolloverAliasName);
        Assertions.assertThat(data.getTemplateName()).isEqualTo(expectedTemplateName);
        Assertions.assertThat(data.getIndexPattern()).isEqualTo(expectedIndexPattern);
    }


}