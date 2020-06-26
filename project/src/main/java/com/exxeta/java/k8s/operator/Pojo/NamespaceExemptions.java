
package com.exxeta.java.k8s.operator.Pojo;

import com.google.gson.annotations.Expose;

import java.util.List;


public class NamespaceExemptions {

    @Expose
    private List<String> namespaces;

    public List<String> getNamespaces() {
        return namespaces;
    }


}
