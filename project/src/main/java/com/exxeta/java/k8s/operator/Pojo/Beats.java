
package com.exxeta.java.k8s.operator.Pojo;

import com.google.gson.annotations.Expose;

import java.util.List;


public class Beats {

    @Expose
    private List<Beat> beats;

    public List<Beat> getBeats() {
        return beats;
    }

    public void setBeats(List<Beat> beats) {
        this.beats = beats;
    }

}
