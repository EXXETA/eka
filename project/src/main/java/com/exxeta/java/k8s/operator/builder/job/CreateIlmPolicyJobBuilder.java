package com.exxeta.java.k8s.operator.builder.job;

import com.exxeta.java.k8s.operator.Pojo.Beat;
import com.exxeta.java.k8s.operator.job.CreateIlmPolicyJob;
import com.exxeta.java.k8s.operator.job.Job;
import com.exxeta.java.k8s.operator.service.ElasticService;
import com.exxeta.java.k8s.operator.tasks.IlmPolicy.GetIlmPolicyTask;
import com.exxeta.java.k8s.operator.tasks.IlmPolicy.PutIlmPolicyTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateIlmPolicyJobBuilder {
    private static Logger LOGGER = LoggerFactory.getLogger(CreateIlmPolicyJobBuilder.class);
    private final String ilmPolicyForceUpdate;
    private final ElasticService elasticService;

    public CreateIlmPolicyJobBuilder(ElasticService elasticService, String ilmPolicyForceUpdate) {
        this.elasticService = elasticService;
        this.ilmPolicyForceUpdate = ilmPolicyForceUpdate;
    }

    public Job buildJob (Beat beat){
        GetIlmPolicyTask getIlmPolicyTask = new GetIlmPolicyTask(elasticService, beat.getIlmPolicy().getName());
        PutIlmPolicyTask putIlmPolicyTask = new PutIlmPolicyTask(elasticService, beat.getIlmPolicy().getName(), beat.getIlmPolicy().getRequestBody());
        return new CreateIlmPolicyJob(ilmPolicyForceUpdate, getIlmPolicyTask, putIlmPolicyTask, beat.getIlmPolicy().getName(), beat);
    }
}
