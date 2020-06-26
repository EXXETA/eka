package com.exxeta.java.k8s.operator.builder.job;

import com.exxeta.java.k8s.operator.Pojo.Beat;
import com.exxeta.java.k8s.operator.Pojo.Beats;
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


public class BeatFallbackIndexJobBuilder {

    private static Logger LOGGER = LoggerFactory.getLogger(BeatFallbackIndexJobBuilder.class);
    private final Map<Environments, String> configMap;
    private final Beats beats;
    private final ElasticService elasticService;

    public BeatFallbackIndexJobBuilder(Map<Environments, String> configMap, Beats beats, ElasticService elasticService) {
        this.configMap = configMap;
        this.beats = beats;
        this.elasticService = elasticService;
    }

    public RootJob build () {
        LOGGER.info("Building added namespace event job");
        CreateTemplateAndIndexJobBuilder jobBuilder = new CreateTemplateAndIndexJobBuilder(elasticService, configMap);
        Set<Job> jobs = new HashSet<>();

        for(Beat beat : beats.getBeats()) {
            jobs.add(jobBuilder.buildJobFallbackIndex(beat));
        }

        //Jobs
        ParallelJob addedNamespaceJob = new ParallelJob(jobs, 10, "Creating Fallback Indices for all Beats", configMap.get(Environments.SLEEP_TIMER));
        return new RootJob(addedNamespaceJob, configMap.get(Environments.DRY_RUN));
    }
}
