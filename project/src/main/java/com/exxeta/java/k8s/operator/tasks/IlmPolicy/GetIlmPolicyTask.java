package com.exxeta.java.k8s.operator.tasks.IlmPolicy;

import com.exxeta.java.k8s.operator.service.ElasticService;
import com.exxeta.java.k8s.operator.tasks.Task;

public class GetIlmPolicyTask extends Task  {

    public GetIlmPolicyTask(ElasticService elasticService, String ilmPolicyName){
        this.elasticService = elasticService;
        this.method = "GET";
        this.endpoint = "_ilm/policy/" + ilmPolicyName;
        this.requestBody = "{}";
        this.log = "Request: Get ILM Policy: '" + ilmPolicyName + "'";
    }

    @Override
    public String getDescription() {
        return "GetIlmPolicyTask";
    }
}
