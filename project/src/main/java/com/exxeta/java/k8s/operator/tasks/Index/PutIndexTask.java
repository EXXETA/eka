package com.exxeta.java.k8s.operator.tasks.Index;

import com.exxeta.java.k8s.operator.service.ElasticService;
import com.exxeta.java.k8s.operator.tasks.Task;

public class PutIndexTask extends Task  {

    public PutIndexTask(ElasticService elasticService, String indexName, String requestBody){
        this.elasticService = elasticService;
        this.method ="PUT";
        this.endpoint = "/" + indexName;
        this.requestBody = requestBody;
        this.log = "Index - Request to create Index: " + indexName;
    }


    @Override
    public String getDescription() {
        return "PutIndexTask";
    }
}
