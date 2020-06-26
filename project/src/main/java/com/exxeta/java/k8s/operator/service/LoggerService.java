package com.exxeta.java.k8s.operator.service;

import com.exxeta.java.k8s.operator.job.RootJob;
import com.exxeta.java.k8s.operator.utility.JsonArrayComparator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.elasticsearch.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class LoggerService {

    private static Logger LOGGER = LoggerFactory.getLogger(LoggerService.class);
    private String path = "/usr/share/efk-controller-logs/logs.json";

    public String readResponseContent(Response response) {
        try (InputStreamReader isReader = new InputStreamReader(response.getEntity().getContent());
             BufferedReader reader = new BufferedReader(isReader))
        {
            String nextLine;
            StringBuffer content = new StringBuffer();
            while((nextLine = reader.readLine()) != null){
                content.append(nextLine);
            }
            return content.toString();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return "IOException while reading response";
        }
    }

    public void writeToFile(RootJob rootJob){
        File file = new File(path);
        ObjectMapper mapper;
        TypeFactory factory;
        MapType type;
        Map<String, Object> allJobs;

        factory = TypeFactory.defaultInstance();
        type = factory.constructMapType(HashMap.class, String.class, Object.class);
        mapper = new ObjectMapper();
        try {
            allJobs = mapper.readValue(file, type);
            List<Map<String, Object>> jobList = new ArrayList<>((Collection<? extends Map<String, Object>>) allJobs.get("allJobs"));
            jobList.add(rootJob.buildJobTree());
            jobList.sort(new JsonArrayComparator());
            allJobs.put("allJobs", jobList);
            mapper.writeValue(file, allJobs);

        } catch (IOException e) {
            LOGGER.warn(e.getMessage());
        }
    }
}
