package com.exxeta.java.k8s.operator.job;

import java.util.Map;

public interface Job {
    boolean execute();
    String getDescription();
    Map<String, Object> buildJobTree ();
    boolean getSuccessful();
}
