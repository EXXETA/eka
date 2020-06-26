package com.exxeta.java.k8s.operator.builder;

import com.exxeta.java.k8s.operator.job.Job;

import java.util.Arrays;
import java.util.Collection;

public class JobCollectionBuilder {


    public <T extends Collection> T build(T collection, Job... jobs) {
        collection.addAll(Arrays.asList(jobs));
        return collection;
    }
}
