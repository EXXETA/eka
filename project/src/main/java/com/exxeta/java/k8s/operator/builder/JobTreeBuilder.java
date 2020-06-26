package com.exxeta.java.k8s.operator.builder;

import com.exxeta.java.k8s.operator.job.Job;
import com.exxeta.java.k8s.operator.utility.JsonArrayComparator;

import java.util.*;

public class JobTreeBuilder {

    public <T extends Collection<Job>> Map<String, Object> buildStringRefJobList(String description, T jobs) {
        Map<String, Object> tree = new HashMap<>();
        List<Map<String, Object>> jobList = new ArrayList<>();
        for (Job job : jobs) {
            jobList.add(job.buildJobTree());
        }
        jobList.sort(new JsonArrayComparator());
        tree.put(description, jobList);
        return tree;
    }

    public  Map<String, Object> buildStringRefJobList(String description, Job... jobs) {
        Map<String, Object> tree = new HashMap<>();
        List<Map<String, Object>> jobList = new ArrayList<>();
        for (Job job : jobs) {
            jobList.add(job.buildJobTree());
        }
        jobList.sort(new JsonArrayComparator());
        tree.put(description, jobList);
        return tree;
    }
}
