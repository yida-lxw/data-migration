package com.yida.datamigration.service;

import com.yida.datamigration.config.ElasticsearchIndexConfig;
import com.yida.datamigration.config.LuceneConfig;
import com.yida.datamigration.elasticsearch.ElasticsearchIndexField;
import org.apache.lucene.search.IndexSearcher;

import java.util.List;

/**
 * 索引数据迁移接口
 */
public interface IndexMigration {
    /**
     * 索引数据迁移
     * @param luceneConfig
     * @param elasticsearchIndexConfig
     * @param indexSearcher
     * @return
     */
    boolean doMigration(LuceneConfig luceneConfig, ElasticsearchIndexConfig elasticsearchIndexConfig,
                        IndexSearcher indexSearcher);



    /**
     * 判断索引是否存在
     * @param indexName
     * @return
     */
    boolean ifIndexExists(String indexName);

    /**
     * 将配置的索引Field列表对象转换成索引映射JSON字符串
     * @param fields
     * @return
     */
    String buildIndexMappingsJSONString(List<ElasticsearchIndexField> fields);

    /**
     * 创建Elasticsearch 索引
     * @param indexName
     * @param shards
     * @param replicas
     * @param indexMappings
     * @return
     */
    boolean createIndex(String indexName, int shards, int replicas, String indexMappings);

    /**
     * 往指定索引中添加一个Document(JSON字符串格式)
     * @param indexName
     * @param indexDocumentJSON
     * @param id
     * @param createOnDuplicated
     * @return
     */
    boolean addDocument(String indexName, String indexDocumentJSON, String id, boolean createOnDuplicated);

    /**
     * 批量往指定索引中添加Document(JSON字符串格式)
     * @param indexName
     * @param indexDocumentJSONList
     * @param batchSize
     * @param primaryField
     * @return
     */
    boolean bulkAddDocuments(String indexName, List<String> indexDocumentJSONList, int batchSize,
                             String primaryField, boolean createOnDuplicated);
}
