package com.exxeta.java.k8s.operator.tasks.Index;

import com.exxeta.java.k8s.operator.service.ElasticService;
import com.exxeta.java.k8s.operator.tasks.Task;

public class GetIndexTemplateTask extends Task {

    public GetIndexTemplateTask(ElasticService elasticService, String ilmTemplateName) {

        this.elasticService = elasticService;
        this.method ="GET";
        this.endpoint = "/_template/" + ilmTemplateName;
        this.requestBody = "{}";
        this.log = "Getting ILM Template : '" + ilmTemplateName + "'";
    }

    @Override
    public String getDescription() {
        return "GetIndexTemplateTask";
    }
}
