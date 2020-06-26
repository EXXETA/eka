package com.exxeta.java.k8s.operator.builder.job;

import com.exxeta.java.k8s.operator.Pojo.Beat;
import com.exxeta.java.k8s.operator.Pojo.NamespaceData;
import com.exxeta.java.k8s.operator.builder.NamespaceNamesBuilder;
import com.exxeta.java.k8s.operator.config.Environments;
import com.exxeta.java.k8s.operator.job.CreateTemplateAndIndexJob;
import com.exxeta.java.k8s.operator.job.Job;
import com.exxeta.java.k8s.operator.service.ElasticService;
import com.exxeta.java.k8s.operator.tasks.Index.GetAliasTask;
import com.exxeta.java.k8s.operator.tasks.Index.PutIndexTask;
import com.exxeta.java.k8s.operator.tasks.Index.PutIndexTemplateTask;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.exxeta.java.k8s.operator.config.Environments.*;

public class CreateTemplateAndIndexJobBuilder {

    private static Logger LOGGER = LoggerFactory.getLogger(CreateTemplateAndIndexJobBuilder.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<Environments, String> configMap;
    private final NamespaceNamesBuilder namespaceNamesBuilder = new NamespaceNamesBuilder();
    private final ElasticService elasticService;

    public CreateTemplateAndIndexJobBuilder(ElasticService elasticService, Map<Environments, String> configMap) {
        this.configMap = configMap;
        this.elasticService = elasticService;
    }

    public Job buildJob(String namespaceName, Beat beat) {
        LOGGER.debug("Build job index for namespace '{}' and beat '{}'", namespaceName, beat.getBeatName());
        String description = "Create Index and Template for Namespace: " + namespaceName + " and Beat: " + beat.getBeatName();
        NamespaceData names = namespaceNamesBuilder.buildForJob(namespaceName, beat);
        String indexTemplateRequestBody = buildIndexTemplateRequestBody(names, beat, configMap.get(PUT_INDEX_TEMPLATE_REQUEST_BODY));
        return createJob(names, indexTemplateRequestBody, beat, description);
    }


    public Job buildJobWithIndexAlias(String namespaceName, Beat beat) {
        LOGGER.debug("Build job index with alias for namespace '{}' and beat '{}'", namespaceName, beat.getBeatName());
        String description = "Create Index with Alias and Template for Namespace: " + namespaceName + " and Beat: " + beat.getBeatName();
        NamespaceData names = namespaceNamesBuilder.buildForJobWithIndexAlias(namespaceName, beat);
        String indexTemplateRequestBody = buildIndexTemplateRequestBody(names, beat, mergeJson(configMap.get(PUT_INDEX_TEMPLATE_REQUEST_BODY), configMap.get(ALIAS_JSON)));
        return createJob(names, indexTemplateRequestBody, beat, description);
    }

    public Job buildJobFallbackIndex(Beat beat) {
        LOGGER.debug("Build job fallback index for  and beat '{}'", beat.getBeatName());
        String description = "Create Fallback Index and Template for " + beat.getBeatName();
        NamespaceData names = namespaceNamesBuilder.buildForJobFallbackIndex(beat);
        String indexTemplateRequestBody = buildIndexTemplateRequestBody(names, beat, configMap.get(PUT_INDEX_TEMPLATE_REQUEST_BODY));
        return createJob(names, indexTemplateRequestBody, beat, description);
    }

    Job createJob(NamespaceData names, String indexTemplateRequestBody, Beat beat, String description) {
        String indexName = names.getIndexName();
        String indexTemplateName = names.getTemplateName();
        String putIndexRequestBody = configMap.get(PUT_INDEX_REQUEST_BODY).replace("$rolloverAlias", names.getRolloverAliasName());

        //Tasks
        GetAliasTask getRolloverAliasTask = new GetAliasTask(elasticService, names.getRolloverAliasName());
        PutIndexTemplateTask putIndexTemplateTask = new PutIndexTemplateTask(elasticService, indexTemplateName, indexTemplateRequestBody);
        PutIndexTask putIndexTask = new PutIndexTask(elasticService, indexName, putIndexRequestBody);

        return new CreateTemplateAndIndexJob(getRolloverAliasTask, putIndexTemplateTask, putIndexTask, beat, description);
    }

    String buildIndexTemplateRequestBody(NamespaceData names, Beat beat, String indexTemplateRequestBody) {
        return indexTemplateRequestBody
                .replace("$indexPattern", names.getIndexPattern())
                .replace("$policyName", beat.getIlmPolicy().getName())
                .replace("$rolloverAlias", names.getRolloverAliasName())
                .replace("$alias", names.getAliasName());
    }

    String mergeJson(String json1, String json2) {

        try {
            Map map1 = objectMapper.readValue(json1, Map.class);
            Map map2 = objectMapper.readValue(json2, Map.class);
            HashMap merged = new HashMap<>(map1);
            merged.putAll(map2);
            return objectMapper.writeValueAsString(merged);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage());
            return "could not merge index template with alias";
        }
    }
}
