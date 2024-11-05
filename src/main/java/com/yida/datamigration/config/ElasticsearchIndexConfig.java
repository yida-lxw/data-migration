package com.yida.datamigration.config;

import com.yida.datamigration.elasticsearch.ElasticsearchIndexField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Elasticsearch Index相关配置
 */
@Component
@Configuration
@EnableConfigurationProperties
@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix = "elasticsearch.index")
public class ElasticsearchIndexConfig {
    private static final Logger log = LoggerFactory.getLogger(ElasticsearchIndexConfig.class);

    /**索引名称*/
    @Value("${index-name}")
    private String indexName;

    /**索引中作为主键的field名称*/
    @Value("${primary-field}")
    private String primaryField;

    /**当索引发生重复，直接创建新的Document或者在写入Document时进行覆盖, true=新建Document不覆盖,false=覆盖旧Document*/
    @Value("${create-on-duplicated}")
    private boolean createOnDuplicated;

    /**索引分片个数*/
    @Value("${shards-num}")
    private int shards;

    /**索引副本个数*/
    @Value("${replicas-num}")
    private int replicas;

    /**数据迁移时每个批次写入的索引文档个数*/
    @Value("${batch-size}")
    private int batchSize;

    /**当前索引的Field列表*/
    //加了@Value注解反而映射不出来
    //@Value("${fields}")
    private List<ElasticsearchIndexField> fields;

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getPrimaryField() {
        return primaryField;
    }

    public void setPrimaryField(String primaryField) {
        this.primaryField = primaryField;
    }

    public boolean isCreateOnDuplicated() {
        return createOnDuplicated;
    }

    public void setCreateOnDuplicated(boolean createOnDuplicated) {
        this.createOnDuplicated = createOnDuplicated;
    }

    public int getShards() {
        return shards;
    }

    public void setShards(int shards) {
        this.shards = shards;
    }

    public int getReplicas() {
        return replicas;
    }

    public void setReplicas(int replicas) {
        this.replicas = replicas;
    }

    public List<ElasticsearchIndexField> getFields() {
        return fields;
    }

    public void setFields(List<ElasticsearchIndexField> fields) {
        this.fields = fields;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }
}
