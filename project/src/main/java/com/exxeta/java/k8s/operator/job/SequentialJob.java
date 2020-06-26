package com.exxeta.java.k8s.operator.job;

import com.exxeta.java.k8s.operator.builder.JobTreeBuilder;
import com.exxeta.java.k8s.operator.utility.JobUtilityMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

public class SequentialJob implements Job {

    private static Logger LOGGER = LoggerFactory.getLogger(SequentialJob.class);
    private final String description;
    private final UUID id;
    private Queue<Job> jobQueue;
    private Queue<Job> unfinishedJobs = new LinkedList<>();
    private final int maximumTries;
    private int tries = 0;
    private long sleepTimer;
    private boolean successful = false;
    private JobUtilityMethods utility = new JobUtilityMethods();



    public SequentialJob(Queue<Job> jobQueue, int maximumTries, String description, String sleepTimer ) {
        this.jobQueue = jobQueue;
        unfinishedJobs.addAll(jobQueue);
        this.maximumTries = maximumTries;
        this.description = description;
        this.id = UUID.randomUUID();
        this.sleepTimer = utility.getSleepTimer(sleepTimer);
    }


    @Override
    public boolean execute() {
        LOGGER.info("Executing Job {} : {}", id, description);
        boolean exceededMaximumTries = tries >= maximumTries;
        while (!unfinishedJobs.isEmpty() && !exceededMaximumTries) {
            Job job = unfinishedJobs.peek();
            if (job != null) {
                if (job.execute()) {
                    unfinishedJobs.poll();
                } else {
                    utility.sleep(sleepTimer, tries);
                    tries++;
                }
            } else {
                unfinishedJobs.poll();
            }
            exceededMaximumTries = tries >= maximumTries;
        }
        successful = !exceededMaximumTries;
        return successful;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Map<String, Object> buildJobTree() {
        JobTreeBuilder jobTreeBuilder = new JobTreeBuilder();
        return jobTreeBuilder.buildStringRefJobList(getDescription(), jobQueue);
    }

    @Override
    public boolean getSuccessful() {
        return successful;
    }
}
