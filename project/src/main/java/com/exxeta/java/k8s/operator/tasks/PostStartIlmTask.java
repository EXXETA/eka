package com.exxeta.java.k8s.operator.tasks;

import com.exxeta.java.k8s.operator.service.ElasticService;

public class PostStartIlmTask extends Task {

    public PostStartIlmTask(ElasticService elasticService){

        this.elasticService = elasticService;
        this.method ="POST";
        this.endpoint = "_ilm/start";
        this.requestBody = "{}";
        this.log = "IndexLifecycleManagement - Request to start";
    }


    @Override
    public String getDescription() {
        return "PostStartIlmTask";
    }
}
