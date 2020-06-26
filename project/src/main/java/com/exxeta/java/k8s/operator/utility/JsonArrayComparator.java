package com.exxeta.java.k8s.operator.utility;

import java.util.*;

public class JsonArrayComparator implements Comparator <Map<String, Object>>{

    @Override
    public int compare(Map<String, Object> o1, Map<String, Object> o2) {

        List<String> keys =  new ArrayList<>();
        keys.addAll(o1.keySet());
        keys.addAll(o2.keySet());
        Collections.sort(keys, String.CASE_INSENSITIVE_ORDER);

        if(o2.keySet().contains(keys.get(0))) {
            return 1;
        } else {
            return -1;
        }
    }
}
