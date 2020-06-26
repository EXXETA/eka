package com.exxeta.java.k8s.operator.tasks.Index;

import com.exxeta.java.k8s.operator.service.ElasticService;
import com.exxeta.java.k8s.operator.tasks.Task;

public class PutIndexTemplateTask extends Task  {

    public PutIndexTemplateTask(ElasticService elasticService, String indexTemplateName, String putIndexTemplateRequestBody){
        this.elasticService = elasticService;
        this.method ="PUT";
        this.endpoint = "_template/" + indexTemplateName;
        this.requestBody = putIndexTemplateRequestBody;
        this.log = "IndexLifecycleManagementTemplate - Request to create new template: " + indexTemplateName;
    }


    @Override
    public String getDescription() {
        return "PutIndexTemplateTask";
    }
}
