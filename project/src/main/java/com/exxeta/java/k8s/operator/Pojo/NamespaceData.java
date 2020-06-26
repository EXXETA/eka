package com.exxeta.java.k8s.operator.Pojo;

public class NamespaceData {

    private String namespaceName;
    private String indexName;
    private String aliasName;
    private String templateName;
    private String rolloverAliasName;
    private String indexPattern;

    public NamespaceData() {    }

    public String getNamespaceName() {
        return namespaceName;
    }

    public void setNamespaceName(String namespaceName) {
        this.namespaceName = namespaceName;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getRolloverAliasName() {
        return rolloverAliasName;
    }

    public void setRolloverAliasName(String rolloverAliasName) {
        this.rolloverAliasName = rolloverAliasName;
    }

    public String getIndexPattern() {
        return indexPattern;
    }

    public void setIndexPattern(String indexPattern) {
        this.indexPattern = indexPattern;
    }
}
