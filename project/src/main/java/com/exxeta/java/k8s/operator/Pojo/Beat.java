
package com.exxeta.java.k8s.operator.Pojo;

import com.google.gson.annotations.Expose;

import java.util.Objects;
import java.util.Optional;


public class Beat {

    @Expose
    private String beatName;
    @Expose
    private IlmPolicy ilmPolicy;
    @Expose
    private String indexSuffix;
    @Expose
    private String version;
    @Expose
    private String fallbackIndexName;
    private boolean initialized = false;

    public String getFallbackIndexName() { return fallbackIndexName;   }

    public void setFallbackIndexName(String fallbackIndexName) { this.fallbackIndexName = fallbackIndexName;  }

    public String getBeatName() {
        return beatName;
    }

    public void setBeatName(String beatName) {
        this.beatName = beatName;
    }

    public IlmPolicy getIlmPolicy() {
        return ilmPolicy;
    }

    public void setIlmPolicy(IlmPolicy ilmPolicy) {
        this.ilmPolicy = ilmPolicy;
    }

    public String getIndexSuffix() {
        return indexSuffix;
    }

    public void setIndexSuffix(String indexSuffix) {
        this.indexSuffix = indexSuffix;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Beat) {
            boolean equal = this.beatName.equals(((Beat) obj).beatName) &&
                    this.indexSuffix.equals(((Beat) obj).indexSuffix) &&
                    this.ilmPolicy.equals(((Beat) obj).ilmPolicy) &&
                    this.version.equals(((Beat) obj).version) &&
                    this.fallbackIndexName.equals(((Beat) obj).fallbackIndexName);
            return equal;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder()
                .append("beatName: " + Optional.ofNullable(this.beatName).orElse("null"))
                .append("indexSuffix: " + Optional.ofNullable(this.indexSuffix).orElse("null"))
                .append("version: " +  Optional.ofNullable(this.version).orElse("null"))
                .append("fallbackIndexName: " +  Optional.ofNullable(this.fallbackIndexName).orElse("null"))
                .append("ilmPolicy: " + Optional.ofNullable(this.ilmPolicy).map(ilmPolicy -> ilmPolicy.toString()).orElse("null"));
        return sb.toString();
    }

    public boolean hasNullValues(){
        return  Objects.isNull(this.beatName) ||
                Objects.isNull(this.indexSuffix) ||
                Objects.isNull(this.version) ||
                Objects.isNull(this.ilmPolicy);
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }



}
