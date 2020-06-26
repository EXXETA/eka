package com.exxeta.java.k8s.operator.job;

import com.exxeta.java.k8s.operator.Pojo.Beat;
import com.exxeta.java.k8s.operator.builder.JobTreeBuilder;
import com.exxeta.java.k8s.operator.tasks.IlmPolicy.GetIlmPolicyTask;
import com.exxeta.java.k8s.operator.tasks.IlmPolicy.PutIlmPolicyTask;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CreateIlmPolicyJob implements Job {

    private static Logger LOGGER = LoggerFactory.getLogger(CreateIlmPolicyJob.class);
    private final String ilmPolicyForceUpdate;
    private final GetIlmPolicyTask getPolicyTask;
    private final PutIlmPolicyTask putIlmPolicyTask;
    private final String policyName;
    private Beat beat;
    private boolean successful = false;

    public CreateIlmPolicyJob(String ilmPolicyForceUpdate, GetIlmPolicyTask getPolicyTask, PutIlmPolicyTask putIlmPolicyTask, String policyName, Beat beat){

        this.ilmPolicyForceUpdate = ilmPolicyForceUpdate;
        this.getPolicyTask = getPolicyTask;
        this.putIlmPolicyTask = putIlmPolicyTask;
        this.policyName = policyName;
        this.beat = beat;
    }

    @Override
    public boolean execute() {
        if (ilmPolicyForceUpdate.equals("true")) {
            LOGGER.info("Doing force update on ILM Policy");
            if(putIlmPolicyTask.execute() && putIlmPolicyTask.getResponse().getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                successful = true;
            } else {
                successful = false;
            }
        } else {
            LOGGER.info("Doing no force update on ILM Policy");
            if(getPolicyTask.execute()){
                successful = createPolicyNoForceUpdate();
            } else {
                LOGGER.warn("No response available");
                successful = false;
            }
        }

        beat.setInitialized(successful);
        return  successful;
    }

    @Override
    public String getDescription() {
        return "CreateIlmPolicyJob - " + policyName;
    }

    @Override
    public Map<String, Object> buildJobTree() {
        JobTreeBuilder jobTreeBuilder = new JobTreeBuilder();
        return jobTreeBuilder.buildStringRefJobList(getDescription(), putIlmPolicyTask, getPolicyTask);
    }

    @Override
    public boolean getSuccessful() {
        return successful;
    }

    boolean createPolicyNoForceUpdate(){
        if (getPolicyTask.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
            LOGGER.info("ILM Policy not found");
            if( putIlmPolicyTask.execute() && putIlmPolicyTask.getResponse().getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                return true;
            }else  {
                return false;
            }
        } else if (getPolicyTask.getStatusCode() == HttpStatus.SC_OK) {
            LOGGER.info("ILM Policy was found - no further action required");
            return true;
        } else {
            LOGGER.info("Unhandled HttpStatus");
            return false;
        }
    }
}
