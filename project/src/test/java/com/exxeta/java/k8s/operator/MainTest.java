package com.exxeta.java.k8s.operator;


import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.exxeta.java.k8s.operator.Pojo.Beat;
import com.exxeta.java.k8s.operator.Pojo.Beats;
import com.exxeta.java.k8s.operator.Pojo.IlmPolicy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class MainTest {


    @Test
    public void agentsJsonToPojo_works() throws JsonProcessingException {

        //given
        String json =
                "{\n" +
                        "      \"beats\" : [\n" +
                        "        {\n" +
                        "          \"beatName\":\"filebeat\",\n" +
                        "          \"indexSuffix\":\"logs\",\n" +
                        "          \"version\":\"7.5.1\",\n" +
                        "            \"ilmPolicy\":{\n" +
                        "              \"name\":\"logs-policy\",\n" +
                        "              \"requestBody\":\"{ body }\"\n" +
                        "            }\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"beatName\":\"metricbeat\",\n" +
                        "          \"indexSuffix\":\"metrics\",\n" +
                        "          \"version\":\"7.4.0\",\n" +
                        "          \"ilmPolicy\":{\n" +
                        "            \"name\":\"metrics-policy\",\n" +
                        "            \"requestBody\":\"{ body }\"\n" +
                        "          }\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    }";
        ObjectMapper objectMapper = new ObjectMapper();
        //expected
        IlmPolicy metricPolicy = new IlmPolicy();
        metricPolicy.setName("metrics-policy");
        metricPolicy.setRequestBody("{ body }");

        Beat metricbeat = new Beat();
        metricbeat.setBeatName("metricbeat");
        metricbeat.setIndexSuffix("metrics");
        metricbeat.setVersion("7.4.0");
        metricbeat.setIlmPolicy(metricPolicy);

        //expected
        IlmPolicy logsPolicy = new IlmPolicy();
        logsPolicy.setName("logs-policy");
        logsPolicy.setRequestBody("{ body }");

        Beat logs = new Beat();
        logs.setBeatName("filebeat");
        logs.setIndexSuffix("logs");
        logs.setVersion("7.5.1");
        logs.setIlmPolicy(logsPolicy);

        //then
        Beats beats = objectMapper.readValue(json, Beats.class);
        //verify
        Assertions.assertThat(beats.getBeats().get(0)).isEqualToComparingFieldByField(logs);
        Assertions.assertThat(beats.getBeats().get(1)).isEqualToComparingFieldByField(metricbeat);
    }



}
