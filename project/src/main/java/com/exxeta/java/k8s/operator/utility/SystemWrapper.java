package com.exxeta.java.k8s.operator.utility;

public class SystemWrapper {

    public String getenv(String name){
        return System.getenv(name);
    }
}
