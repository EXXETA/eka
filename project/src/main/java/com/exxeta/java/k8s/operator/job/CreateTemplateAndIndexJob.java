package com.exxeta.java.k8s.operator.job;

import com.exxeta.java.k8s.operator.Pojo.Beat;
import com.exxeta.java.k8s.operator.builder.JobTreeBuilder;
import com.exxeta.java.k8s.operator.tasks.Index.GetAliasTask;
import com.exxeta.java.k8s.operator.tasks.Index.PutIndexTask;
import com.exxeta.java.k8s.operator.tasks.Index.PutIndexTemplateTask;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CreateTemplateAndIndexJob implements Job {

    private static Logger LOGGER = LoggerFactory.getLogger(CreateTemplateAndIndexJob.class);
    private final GetAliasTask getRolloverAliasTask;
    private final PutIndexTemplateTask putIndexTemplateTask;
    private final PutIndexTask putIndexTask;
    private final String description;
    private Beat beat;
    private boolean getRolloverSuccess = false;
    private boolean putIndexTemplateSuccess = false;
    private boolean putIndexSuccess = false;

    public CreateTemplateAndIndexJob(GetAliasTask getRolloverAliasTask, PutIndexTemplateTask putIndexTemplateTask, PutIndexTask putIndexTask, Beat beat, String description) {

        this.getRolloverAliasTask = getRolloverAliasTask;
        this.putIndexTemplateTask = putIndexTemplateTask;
        this.putIndexTask = putIndexTask;
        this.beat = beat;
        this.description = description;
    }


    @Override
    public boolean execute() {
        if (!beat.isInitialized()) {
            return false;
        } else {
            return executeAndEvaluateRolloverTask();
        }
    }

    private boolean executeAndEvaluateRolloverTask() {
        getRolloverSuccess = getRolloverAliasTask.execute();
        if (getRolloverSuccess) {
            int statusCode = getRolloverAliasTask.getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                LOGGER.info("Rollover Alias found - no further action required");
                putIndexSuccess = true;
                putIndexTemplateSuccess = true;
            } else if (statusCode == HttpStatus.SC_NOT_FOUND) {
                LOGGER.info("Rollover Alias not found - creating index template and index");
                return executeAndEvaluatePutIndexTemplateTask();
            } else {
                LOGGER.warn("Unhandled HttpStatus");
            }
        } else {
            LOGGER.warn("No Response available in getRolloverAliasTask");
        }
        return getSuccessful();
    }

    private boolean executeAndEvaluatePutIndexTemplateTask() {
        if ( putIndexTemplateTask.execute()) {
            int statusCode = putIndexTemplateTask.getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                LOGGER.info("Template Creation successful. Trying to create Index");
                putIndexTemplateSuccess = true;
                return executeAndEvaluatePutIndexTask();
            } else {
                LOGGER.warn("Unhandled HttpStatus");
            }
        } else {
            LOGGER.warn("No Response available in PutIndexTemplateTask");
        }
        return getSuccessful();
    }

    private boolean executeAndEvaluatePutIndexTask() {

        if (putIndexTask.execute()) {
            int statusCode = putIndexTask.getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                LOGGER.info("Index and Template creation successful");
                putIndexSuccess = true;
            } else {
                LOGGER.info("Unhandled HttpStatus");
            }
        } else {
            LOGGER.info("No Response available in PutIndexTask");
        }
        return getSuccessful();
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Map<String, Object> buildJobTree() {
        JobTreeBuilder jobTreeBuilder = new JobTreeBuilder();
        return jobTreeBuilder.buildStringRefJobList(getDescription(), getRolloverAliasTask, putIndexTemplateTask, putIndexTask);
    }

    @Override
    public boolean getSuccessful() {
        return getRolloverSuccess && putIndexSuccess && putIndexTemplateSuccess;
    }

}
