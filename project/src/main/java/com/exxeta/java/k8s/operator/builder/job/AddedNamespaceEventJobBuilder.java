package com.exxeta.java.k8s.operator.builder.job;

import com.exxeta.java.k8s.operator.Pojo.Beat;
import com.exxeta.java.k8s.operator.Pojo.Beats;
import com.exxeta.java.k8s.operator.Pojo.NamespaceExemptions;
import com.exxeta.java.k8s.operator.config.Environments;
import com.exxeta.java.k8s.operator.job.Job;
import com.exxeta.java.k8s.operator.job.ParallelJob;
import com.exxeta.java.k8s.operator.job.RootJob;
import com.exxeta.java.k8s.operator.service.ElasticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AddedNamespaceEventJobBuilder {

    private static Logger LOGGER = LoggerFactory.getLogger(AddedNamespaceEventJobBuilder.class);
    private final Map<Environments, String> configMap;
    private final Beats beats;
    private final NamespaceExemptions namespaceExemptions;

    public AddedNamespaceEventJobBuilder(Map<Environments,String> configMap, Beats beats, NamespaceExemptions namespaceExemptions) {
        this.configMap = configMap;
        this.beats = beats;
        this.namespaceExemptions = namespaceExemptions;
    }


    public RootJob buildAddedEvent(ElasticService elasticService, String namespaceName) {

        LOGGER.debug("Building added namespace event Job");
        String description = "Added namespace event Job. Namespace: " + namespaceName;
        CreateTemplateAndIndexJobBuilder jobBuilder = new CreateTemplateAndIndexJobBuilder(elasticService, configMap);
        Set<Job> jobs = new HashSet<>();

        for(Beat beat : beats.getBeats()) {
            if(!namespaceExemptions.getNamespaces().contains(namespaceName)){
                jobs.add(jobBuilder.buildJob(namespaceName, beat));
            } else {
                jobs.add(jobBuilder.buildJobWithIndexAlias(namespaceName, beat));
            }

        }

        //Jobs
        ParallelJob addedNamespaceJob = new ParallelJob(jobs, 10, description, configMap.get(Environments.SLEEP_TIMER));
        LOGGER.debug("Done building added namespace event job");
        return new RootJob(addedNamespaceJob, configMap.get(Environments.DRY_RUN));

    }


}
