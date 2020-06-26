
package com.exxeta.java.k8s.operator.Pojo;

import com.google.gson.annotations.Expose;

import java.util.Objects;
import java.util.Optional;


public class IlmPolicy {

    @Expose
    private String name;
    @Expose
    private String requestBody;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof IlmPolicy){
            boolean equal =
                    this.name.equals(((IlmPolicy) obj).name) &&
                    this.requestBody.equals(((IlmPolicy) obj).requestBody);
            return equal;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder()
                .append("name: " + Optional.ofNullable(this.name).orElse("null"))
                .append("requestBody: " + Optional.ofNullable(this.requestBody).orElse("null"));
        return sb.toString();
    }

    public boolean hasNullValues(){
        return  Objects.isNull(this.name) ||
                Objects.isNull(this.requestBody);
    }
}
