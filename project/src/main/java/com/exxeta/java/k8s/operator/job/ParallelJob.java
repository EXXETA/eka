package com.exxeta.java.k8s.operator.job;

import com.exxeta.java.k8s.operator.builder.JobTreeBuilder;
import com.exxeta.java.k8s.operator.utility.JobUtilityMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ParallelJob implements Job {
    private static Logger LOGGER = LoggerFactory.getLogger(ParallelJob.class);
    private Set<Job> unfinishedJobs = new HashSet<>();
    private boolean successful = false;
    private int tries = 0;
    private JobUtilityMethods utility = new JobUtilityMethods();
    private Set<Job> jobSet;
    private final int maximumTries;
    private String description;
    private long sleepTimer;
    private UUID id;

    public ParallelJob(Set<Job> jobSet, int maximumTries, String description, String sleepTimer) {
        this.jobSet = jobSet;
        unfinishedJobs.addAll(jobSet);
        this.maximumTries = maximumTries;
        this.description = description;
        id = UUID.randomUUID();
        this.sleepTimer = utility.getSleepTimer(sleepTimer);
    }

    @Override
    public boolean execute() {
        LOGGER.info("Executing Job {} : {}", id, description);
        for (int i = 0; i < maximumTries; i++) {
            if (unfinishedJobs.isEmpty() || tries >= maximumTries) {
                break;
            }
            LOGGER.info("Executing Job {}, Jobexecution Try: {}", id, tries);
            unfinishedJobs = unfinishedJobs.parallelStream().filter(task -> !task.execute()).collect(Collectors.toSet());
            if(!unfinishedJobs.isEmpty()){
                utility.sleep(sleepTimer, tries);
                tries++;
            }
        }
        successful =  unfinishedJobs.isEmpty();
        return successful;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Map<String, Object> buildJobTree() {
        JobTreeBuilder jobTreeBuilder = new JobTreeBuilder();
        return jobTreeBuilder.buildStringRefJobList(getDescription(), jobSet);
    }

    @Override
    public boolean getSuccessful() {
        return successful;
    }
}
