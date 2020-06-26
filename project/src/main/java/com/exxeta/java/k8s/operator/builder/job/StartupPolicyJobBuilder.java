package com.exxeta.java.k8s.operator.builder.job;

import com.exxeta.java.k8s.operator.Pojo.Beat;
import com.exxeta.java.k8s.operator.Pojo.Beats;
import com.exxeta.java.k8s.operator.builder.JobCollectionBuilder;
import com.exxeta.java.k8s.operator.config.Environments;
import com.exxeta.java.k8s.operator.job.Job;
import com.exxeta.java.k8s.operator.job.ParallelJob;
import com.exxeta.java.k8s.operator.job.RootJob;
import com.exxeta.java.k8s.operator.job.SequentialJob;
import com.exxeta.java.k8s.operator.service.ElasticService;
import com.exxeta.java.k8s.operator.tasks.PostStartIlmTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class StartupPolicyJobBuilder {

    private static Logger LOGGER = LoggerFactory.getLogger(StartupPolicyJobBuilder.class);
    private JobCollectionBuilder jobCollectionBuilder = new JobCollectionBuilder();
    private ElasticService elasticService;
    private Map<Environments, String> configMap;
    private Beats beats;
    private CreateIlmPolicyJobBuilder createIlmPolicyJobBuilder;

    public StartupPolicyJobBuilder(ElasticService elasticService, Map<Environments, String> configMap, Beats beats) {
        this.elasticService = elasticService;
        this.configMap = configMap;
        this.beats = beats;
        createIlmPolicyJobBuilder = new CreateIlmPolicyJobBuilder(elasticService, configMap.get(Environments.ILM_POLICY_FORCE_UPDATE));
    }

    public RootJob build(){
        LOGGER.debug("Building Startup Job");
        String description = "Startup Job";
        Set<Job> policyJobs = new HashSet<>();

        PostStartIlmTask activateIlm = new PostStartIlmTask(elasticService);
        for(Beat beat : beats.getBeats()){
            policyJobs.add(createIlmPolicyJobBuilder.buildJob(beat));
        }
        ParallelJob createPolicies = new ParallelJob(policyJobs, 2, "Creating Policies at startup", configMap.get(Environments.SLEEP_TIMER));

        SequentialJob startup = new SequentialJob(jobCollectionBuilder.build(new LinkedList<>(), activateIlm, createPolicies), 10, description, configMap.get(Environments.SLEEP_TIMER));
        return new RootJob(startup, configMap.get(Environments.DRY_RUN));
    }
}
