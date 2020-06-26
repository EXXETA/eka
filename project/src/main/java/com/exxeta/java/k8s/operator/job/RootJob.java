package com.exxeta.java.k8s.operator.job;

import com.exxeta.java.k8s.operator.service.LoggerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RootJob implements Job {
    private static Logger LOGGER = LoggerFactory.getLogger(RootJob.class);
    private LoggerService loggerService = new LoggerService();
    private Job child;
    private boolean success;
    private boolean dryRun;


    public RootJob(Job child, String dryRun) {
        this.child = child;
        this.dryRun = Boolean.parseBoolean(dryRun);
    }

    @Override
    public boolean execute() {
        if(!dryRun){
            success = child.execute();
        } else {
            success = true;
        }
        // write to file
        loggerService.writeToFile(this);
        return success;
    }

    @Override
    public String getDescription() {
        return "root job";
    }

    @Override
    public Map<String, Object> buildJobTree() {
        return child.buildJobTree();
    }

    @Override
    public boolean getSuccessful() {
        return false;
    }


}
