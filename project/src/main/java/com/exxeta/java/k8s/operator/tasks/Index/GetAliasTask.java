package com.exxeta.java.k8s.operator.tasks.Index;

import com.exxeta.java.k8s.operator.service.ElasticService;
import com.exxeta.java.k8s.operator.tasks.Task;

public class GetAliasTask extends Task  {


    public GetAliasTask(ElasticService elasticService, String aliasName) {
        this.elasticService = elasticService;
        this.method ="GET";
        this.endpoint = "/_alias/" + aliasName;
        this.requestBody = "{}";
        this.log = "Getting Alias : '" + aliasName + "'";
    }

    @Override
    public String getDescription() {
        return "GetAliasTask";
    }
}
