package com.exxeta.java.k8s.operator.tasks.Index;

import com.exxeta.java.k8s.operator.service.ElasticService;
import com.exxeta.java.k8s.operator.tasks.Task;

public class DeleteIndexTask extends Task {

    public DeleteIndexTask(ElasticService elasticService, String indexName) {

        this.elasticService = elasticService;
        this.method ="DELETE";
        this.endpoint = "/" + indexName;
        this.requestBody = "{}";
        this.log = "Index - Request to delete Index: " + indexName;
    }

    @Override
    public String getDescription() {
        return "DeleteIndexTask";
    }
}
