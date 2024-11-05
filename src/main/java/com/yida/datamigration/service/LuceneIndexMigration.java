package com.yida.datamigration.service;

import com.yida.datamigration.elasticsearch.ElasticsearchIndexField;
import com.yida.datamigration.utils.ElasticsearchClientUtils;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Lucene索引数据迁移实现
 */
@Component
public class LuceneIndexMigration extends AbstractIndexMigration {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    private static final Logger log = LoggerFactory.getLogger(LuceneIndexMigration.class);

    /**
     * 判断指定的索引是否存在
     *
     * @param indexName
     * @return
     */
    @Override
    public boolean ifIndexExists(String indexName) {
        IndicesClient indicesClient = restHighLevelClient.indices();
        return ElasticsearchClientUtils.isIndexExists(indexName, indicesClient);
    }

    /**
     * 构建索引映射的JSON字符串
     *
     * @param fields
     * @return
     */
    @Override
    public String buildIndexMappingsJSONString(List<ElasticsearchIndexField> fields) {
        return ElasticsearchClientUtils.buildIndexMappings(fields);
    }

    /**
     * 创建索引
     *
     * @param indexName
     * @param shards
     * @param replicas
     * @param indexMappings
     * @return
     */
    @Override
    public boolean createIndex(String indexName, int shards, int replicas, String indexMappings) {
        IndicesClient indicesClient = restHighLevelClient.indices();
        return ElasticsearchClientUtils.createIndex(indexName, indexMappings, indicesClient);
    }

    /**
     * 往索引中添加一个Document(JSON字符串格式)
     *
     * @param indexName
     * @param indexDocumentJSON
     * @return
     */
    @Override
    public boolean addDocument(String indexName, String indexDocumentJSON, String id, boolean createOnDuplicated) {
        return ElasticsearchClientUtils.addDocument(restHighLevelClient, indexName, indexDocumentJSON, id, createOnDuplicated);
    }

    /**
     * 往索引中批量添加多个Document(JSON字符串格式)
     *
     * @param indexName
     * @param indexDocumentJSONList
     * @param batchSize
     * @param primaryField
     * @param createOnDuplicated
     * @return
     */
    @Override
    public boolean bulkAddDocuments(String indexName, List<String> indexDocumentJSONList, int batchSize, String primaryField, boolean createOnDuplicated) {
        return ElasticsearchClientUtils.bulkAddDocuments(restHighLevelClient, indexName, indexDocumentJSONList, batchSize, primaryField, createOnDuplicated);
    }
}
