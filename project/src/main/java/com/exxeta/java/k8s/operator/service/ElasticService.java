package com.exxeta.java.k8s.operator.service;

import com.exxeta.java.k8s.operator.tasks.Task;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class ElasticService {

    private static Logger LOGGER = LoggerFactory.getLogger(ElasticService.class);
    private RestClient clientElastic;
    private LoggerService loggerService;

    public ElasticService(RestClient clientElastic, LoggerService loggerService) {
        this.clientElastic = clientElastic;
        this.loggerService = loggerService;
    }
    public boolean performRequest(Request request, Task task) {
        try {
            task.setResponse(clientElastic.performRequest(request));
            LOGGER.info("Response received");
            LOGGER.debug("Response Json: {}", loggerService.readResponseContent(task.getResponse()));
            return true;
        } catch (ResponseException e) {
            task.setResponse(e.getResponse());
            LOGGER.debug("Response Json: {}", loggerService.readResponseContent(task.getResponse()));
            return responseExceptionAction(e);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return false;
        }
    }

    public boolean responseExceptionAction(ResponseException e) {
        Response response = e.getResponse();
        boolean isPresent = Objects.nonNull(response);
        if (isPresent) {
            LOGGER.info("Response received");
            return true;
        }
        LOGGER.error(e.getMessage());
        return false;

    }


}
