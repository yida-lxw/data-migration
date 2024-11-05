package com.yida.datamigration.elasticsearch;

import java.io.Serializable;

/**
 * Elasticsearch索引Field
 */
public class ElasticsearchIndexField implements Serializable {
    private static final long serialVersionUID = -1403075208015235438L;

    /**默认不存储索引Field原始值至索引数据中*/
    public static final boolean DEFAULT_FIELD_STORE = false;

    /**索引field名称*/
    private String fieldName;

    /**索引field数据类型*/
    private ElasticsearchFieldType fieldType;

    /**索引field原始值是否存储至索引中*/
    private boolean store;

    /**索引field使用的分词器*/
    private String anaylzer;

    public ElasticsearchIndexField() {}

    public ElasticsearchIndexField(String fieldName, ElasticsearchFieldType fieldType) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.store = DEFAULT_FIELD_STORE;
    }

    public ElasticsearchIndexField(String fieldName, ElasticsearchFieldType fieldType, boolean store) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.store = store;
    }

    public ElasticsearchIndexField(String fieldName, ElasticsearchFieldType fieldType, boolean store, String anaylzer) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.store = store;
        this.anaylzer = anaylzer;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public ElasticsearchFieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(ElasticsearchFieldType fieldType) {
        this.fieldType = fieldType;
    }

    public boolean isStore() {
        return store;
    }

    public void setStore(boolean store) {
        this.store = store;
    }

    public String getAnaylzer() {
        return anaylzer;
    }

    public void setAnaylzer(String anaylzer) {
        this.anaylzer = anaylzer;
    }
}
