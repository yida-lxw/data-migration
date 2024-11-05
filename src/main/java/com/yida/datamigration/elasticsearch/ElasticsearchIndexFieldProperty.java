package com.yida.datamigration.elasticsearch;

import java.io.Serializable;

/**
 * Elasticsearch索引Field的属性
 */
public class ElasticsearchIndexFieldProperty implements Serializable {
    private static final long serialVersionUID = 2833037826892557757L;

    /**Field的数据类型*/
    private String type;

    /**是否存储Field的原始值至索引中*/
    private boolean store;

    /**当前Field使用的分词器*/
    private String analyzer;

    public ElasticsearchIndexFieldProperty() {}

    public ElasticsearchIndexFieldProperty(String type, boolean store) {
        this.type = type;
        this.store = store;
    }

    public ElasticsearchIndexFieldProperty(String type, boolean store, String analyzer) {
        this.type = type;
        this.store = store;
        this.analyzer = analyzer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isStore() {
        return store;
    }

    public void setStore(boolean store) {
        this.store = store;
    }

    public String getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(String analyzer) {
        this.analyzer = analyzer;
    }
}
