package com.exxeta.java.k8s.operator.builder;

import com.exxeta.java.k8s.operator.Pojo.Beat;
import com.exxeta.java.k8s.operator.Pojo.NamespaceData;

public class NamespaceNamesBuilder {



    public NamespaceData buildForJob(String namespaceName, Beat beat){
        String baseName = buildName(namespaceName, "k8s", beat.getIndexSuffix(), beat.getVersion());
        return setData(namespaceName, baseName);
    }

    public NamespaceData buildForJobWithIndexAlias(String namespaceName, Beat beat){
        String baseName = buildName(namespaceName, beat.getIndexSuffix(), beat.getVersion());
        return setData(namespaceName, baseName);
    }

    public NamespaceData buildForJobFallbackIndex(Beat beat){
        String baseName = buildName(beat.getBeatName(), "k8s", beat.getVersion());
        return setData("no namespace", baseName);
    }

    NamespaceData setData(String namespaceName, String baseName){
        NamespaceData namespaceData = new NamespaceData();
        namespaceData.setNamespaceName(namespaceName);
        namespaceData.setIndexName(baseName + "-000001");
        namespaceData.setAliasName(baseName + "-alias");
        namespaceData.setRolloverAliasName(baseName + "-rollover");
        namespaceData.setTemplateName(baseName + "-template");
        namespaceData.setIndexPattern(baseName + "-*");
        return namespaceData;
    }

    String buildName(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String current : parts) {
            sb.append("-").append(current);
        }
        return sb.deleteCharAt(0).toString();
    }
}
