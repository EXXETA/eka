package com.exxeta.java.k8s.operator.tasks;

import com.exxeta.java.k8s.operator.job.Job;
import com.exxeta.java.k8s.operator.service.ElasticService;
import com.exxeta.java.k8s.operator.tasks.IlmPolicy.GetIlmPolicyTask;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class Task implements Job {
    protected static Logger LOGGER = LoggerFactory.getLogger(GetIlmPolicyTask.class);
    protected Response response;
    protected ElasticService elasticService;
    protected boolean isDone = false;
    protected String method;
    protected String endpoint;
    protected String requestBody;
    protected String log;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
    @Override
    public boolean getSuccessful() {
        return isDone;
    }

    @Override
    public boolean execute() {
        if(isDone){
            return true;
        } else {
            LOGGER.info(log);
            isDone = executeIfNotDone();
            return isDone;
        }
    }



    protected boolean executeIfNotDone() {
        Request request = new Request(
                method,
                endpoint
        );
        request.setJsonEntity(requestBody);
        return elasticService.performRequest(request,this);
    }

    public Map<String, Object> buildJobTree (){
        Map<String, Object> subTree = new HashMap<>();
        Map<String, Object> mainTree = new HashMap<>();

        subTree.put("Method", method);
        subTree.put("Endpoint", endpoint);
        if(Objects.nonNull(requestBody)) {
            subTree.put("RequestBody", buildRequestBodyTree(requestBody));
        }
        subTree.put("IsDone", isDone);

        mainTree.put(getDescription(), subTree);
        return mainTree;
    }



    private Map<String, Object> buildRequestBodyTree(String requestBody){
        ObjectMapper mapper;
        TypeFactory factory;
        MapType type;

        factory = TypeFactory.defaultInstance();
        type = factory.constructMapType(HashMap.class, String.class, Object.class);
        mapper = new ObjectMapper();
        try {
            return  mapper.readValue(requestBody, type);
        } catch (JsonProcessingException e) {
            LOGGER.warn(e.getMessage());
            Map<String, Object> map = new HashMap<>();
            map.put("JsonProcessingException", "while reading requestBody");
            return map;
        }
    }

    public int getStatusCode(){
        if(isDone){
            return response.getStatusLine().getStatusCode();
        } else{
            LOGGER.warn("Cannot request statuscode if task is not yet done");
            return -1;
        }
    }
}
