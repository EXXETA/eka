package com.exxeta.java.k8s.operator.tasks.IlmPolicy;

import com.exxeta.java.k8s.operator.service.ElasticService;
import com.exxeta.java.k8s.operator.tasks.Task;

public class PutIlmPolicyTask extends Task{

    public PutIlmPolicyTask(ElasticService elasticService, String ilmPolicyName, String putIlmPolicyRequestBody){

        this.elasticService = elasticService;
        this.method ="PUT";
        this.endpoint = "_ilm/policy/" + ilmPolicyName;
        this.requestBody = putIlmPolicyRequestBody;
        this.log = "IndexLifeCyclePolicy - Request to create new policy: " + ilmPolicyName;
    }

    @Override
    public String getDescription() {
        return "PutIlmPolicyTask";
    }
}
