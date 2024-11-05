package com.yida.datamigration.service;

import com.yida.datamigration.config.ElasticsearchIndexConfig;
import com.yida.datamigration.config.LuceneConfig;
import com.yida.datamigration.elasticsearch.ElasticsearchIndexField;
import com.yida.datamigration.lucene.SearchResult;
import com.yida.datamigration.utils.GsonUtils;
import com.yida.datamigration.utils.LuceneUtils;
import com.yida.datamigration.utils.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 索引数据迁移抽象实现
 * 采用模版模式定义数据迁移操作流程
 */
public abstract class AbstractIndexMigration implements IndexMigration {
    private static final Logger log = LoggerFactory.getLogger(AbstractIndexMigration.class);

    @Override
    public boolean doMigration(LuceneConfig luceneConfig, ElasticsearchIndexConfig elasticsearchIndexConfig,
                               IndexSearcher indexSearcher) {
        String indexName = elasticsearchIndexConfig.getIndexName();
        if(StringUtils.isEmpty(indexName)) {
            log.error("Index name is not specified, unable to start index data migration.");
            return false;
        }
        int pageSize = luceneConfig.getPageSize();
        log.info("Plan to load index data by reading [{}] documents from Lucene in one batch", pageSize);
        IndexReader indexReader = indexSearcher.getIndexReader();
        int totalDocsCount = LuceneUtils.getIndexTotalCount(indexReader);
        log.info("There are [{}] index documents had been read from index-dir:[{}]", totalDocsCount, luceneConfig.getIndexBaseDir());
        int totalPageCount = LuceneUtils.calculateTotalPage(totalDocsCount, pageSize);
        log.info("pageSize:[{}] per Batch, totalPageCount:[{}]", pageSize, totalPageCount);

        List<ElasticsearchIndexField> fields = elasticsearchIndexConfig.getFields();
        if(null == fields || fields.size() <= 0) {
            log.error("Index fields is not specified, unable to start index data migration.");
            return false;
        }

        boolean isBulkAddDocumentsSuccess = true;
        //若索引不存在，则新建索引
        if(!ifIndexExists(indexName)) {
            //先根据配置构建索引映射
            String indexMappings = buildIndexMappingsJSONString(fields);
            if(StringUtils.isEmpty(indexMappings)) {
                log.error("Building Index mappings failure with fields-config:[{}], unable to start index data migration.",
                        GsonUtils.getInstance().beanToString(fields));
                return false;
            }
            //新建索引
            int shards = elasticsearchIndexConfig.getShards();
            int replicas = elasticsearchIndexConfig.getReplicas();
            boolean createIndexSuccess = createIndex(indexName, shards, replicas, indexMappings);
            //若创建索引失败
            if(!createIndexSuccess) {
                log.error("Create index failure with indexName:[{}],shards:[{}],replicas:[{}],indexMappings:[{}]",
                        indexName, shards, replicas, indexMappings);
                return false;
            }

            List<String> fieldNameList = fields.stream().map(f -> f.getFieldName()).collect(Collectors.toList());
            String[] fieldList = fieldNameList.toArray(new String[] {});

            String primaryField = elasticsearchIndexConfig.getPrimaryField();
            boolean createOnDuplicated = elasticsearchIndexConfig.isCreateOnDuplicated();
            Query query = new MatchAllDocsQuery();
            Sort sort = new Sort(SortField.FIELD_SCORE);
            SearchResult searchResult = null;
            //一个批次写入的Document个数
            int batchSize = elasticsearchIndexConfig.getBatchSize();
            int currentPage = 1;
            for (int i = 1; i <= totalPageCount ; i++) {
                currentPage = i;
                searchResult = LuceneUtils.pageQuery(indexSearcher, query, sort, currentPage, pageSize);
                List<Document> documentList =  searchResult.getDocumentList();
                if(null != documentList && documentList.size() > 0) {
                    List<String> documentJSONList = LuceneUtils.documents2JSONList(documentList, fieldList);
                    boolean addDocumentSuccess = bulkAddDocuments(indexName, documentJSONList, batchSize,
                            primaryField, createOnDuplicated);
                    if(!addDocumentSuccess) {
                        isBulkAddDocumentsSuccess = false;
                        log.error("Failed to add index in the current batch to bulk add documents to index:[{}]", indexName);
                    } else {
                        log.info("The page-[{}] of index data has been written to the index:[{}] of Elasticsearch " +
                                "successfully.", currentPage, indexName);
                    }
                }
            }
        }
        return isBulkAddDocumentsSuccess;
    }

    @Override
    public boolean ifIndexExists(String indexName) {
        return false;
    }

    @Override
    public String buildIndexMappingsJSONString(List<ElasticsearchIndexField> fields) {
        return null;
    }

    @Override
    public boolean createIndex(String indexName, int shards, int replicas, String indexMappings) {
        return false;
    }

    @Override
    public boolean addDocument(String indexName, String indexDocumentJSON, String id, boolean createOnDuplicated) {
        return false;
    }

    @Override
    public boolean bulkAddDocuments(String indexName, List<String> indexDocumentJSONList, int batchSize,
                                    String primaryField, boolean createOnDuplicated) {
        return false;
    }
}
