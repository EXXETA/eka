package com.exxeta.java.k8s.operator.builder.job;


import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.exxeta.java.k8s.operator.config.Environments;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@RunWith(MockitoJUnitRunner.class)
public class CreateTemplateAndIndexJobBuilderTest {

    private CreateTemplateAndIndexJobBuilder jobBuilder;
    private Map<Environments, String > configMap;
    @Before
    public void init() {
        configMap = new HashMap<>();
        jobBuilder = new CreateTemplateAndIndexJobBuilder(null, configMap);
    }




    @Test
    public void mergeIndexTemplateRequestBodyWithAlias_works() throws JsonProcessingException {
        ObjectMapper ob = new ObjectMapper();
        String json1 = "    {\n" +
                "      \"index_patterns\": [\"$indexPattern\"],\n" +
                "      \"settings\": {\n" +
                "        \"index\": {\n" +
                "          \"lifecycle\": {\n" +
                "            \"name\": \"$policy_name\",\n" +
                "            \"rollover_alias\": \"$rollover_alias\"\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }";
        String json2 = "    {\n" +
                "        \"aliases\" : {\n" +
                "          \"$alias\" : {}\n" +
                "        }\n" +
                "    }";
        configMap.put(Environments.PUT_INDEX_TEMPLATE_REQUEST_BODY, json1);
        configMap.put(Environments.ALIAS_JSON, json2);
        String json_expected ="{\n" +
                "      \"index_patterns\": [\"$indexPattern\"],\n" +
                "      \"settings\": {\n" +
                "        \"index\": {\n" +
                "          \"lifecycle\": {\n" +
                "            \"name\": \"$policy_name\",\n" +
                "            \"rollover_alias\": \"$rollover_alias\"\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      \"aliases\" : {\n" +
                "        \"$alias\" : {}\n" +
                "      }\n" +
                "    }";
        Map<String, Object> expectedMap = ob.readValue(json_expected, Map.class);
        //then
        Map<String, Object> actualMap = ob.readValue(jobBuilder.mergeJson(json1,json2), Map.class);
        //verify
        Assertions.assertThat(actualMap).isEqualTo(expectedMap);
    }
}