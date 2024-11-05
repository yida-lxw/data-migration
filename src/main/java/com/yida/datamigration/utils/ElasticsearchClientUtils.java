package com.yida.datamigration.utils;

import com.yida.datamigration.elasticsearch.ElasticsearchIndexField;
import com.yida.datamigration.elasticsearch.ElasticsearchIndexFieldProperty;
import com.yida.datamigration.elasticsearch.ElasticsearchIndexMapping;
import org.apache.lucene.document.Document;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Elasticsearch Client操作工具类
 */
public class ElasticsearchClientUtils {
    private static final Logger log = LoggerFactory.getLogger(ElasticsearchClientUtils.class);

    /**
     * 构建索引映射JSON字符串
     * @param fields
     * @return
     */
    public static String buildIndexMappings(List<ElasticsearchIndexField> fields) {
        ElasticsearchIndexMapping elasticsearchIndexMapping = new ElasticsearchIndexMapping();
        Map<String, ElasticsearchIndexFieldProperty> properties = new LinkedHashMap<>();
        for(ElasticsearchIndexField elasticsearchIndexField : fields) {
            String fieldName = elasticsearchIndexField.getFieldName();
            ElasticsearchIndexFieldProperty elasticsearchIndexFieldProperty = new ElasticsearchIndexFieldProperty();
            elasticsearchIndexFieldProperty.setType(elasticsearchIndexField.getFieldType().getName());
            elasticsearchIndexFieldProperty.setStore(elasticsearchIndexField.isStore());
            String analyzer = elasticsearchIndexField.getAnaylzer();
            analyzer = (StringUtils.isEmpty(analyzer))? null : analyzer;
            elasticsearchIndexFieldProperty.setAnalyzer(analyzer);
            properties.put(fieldName, elasticsearchIndexFieldProperty);
        }
        elasticsearchIndexMapping.setProperties(properties);
        return GsonUtils.getInstance().beanToString(elasticsearchIndexMapping);
    }

    /**
     * 判断指定索引是否存在
     * @param indexName
     * @param indicesClient
     * @return
     */
    public static boolean isIndexExists(String indexName, IndicesClient indicesClient) {
        try {
            GetIndexRequest getIndexRequest=new GetIndexRequest(indexName);
            return indicesClient.exists(getIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("Call IndicesClient.exists([{}]) occur exception.", indexName, e);
        }
        return false;
    }

    /**
     * 创建索引
     * @param indexName
     * @param indexMappings
     * @param indicesClient
     * @return
     */
    public static boolean createIndex(String indexName, String indexMappings, IndicesClient indicesClient) {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
        createIndexRequest.mapping("_doc", indexMappings, XContentType.JSON);

        boolean createIndexSuccess = false;
        try {
            CreateIndexResponse createIndexResponse = indicesClient.create(createIndexRequest, RequestOptions.DEFAULT);
            createIndexSuccess = createIndexResponse.isAcknowledged();
        } catch (IOException e) {
            log.error("Create Index with indexName:[{}] occur exception.", indexName, e);
        }
        return createIndexSuccess;
    }

    /**
     * 添加单个Document(JSON字符串格式)至Elasticsearch Index中
     * @param esClient
     * @param indexName
     * @param document
     * @param fieldList
     * @return
     */
    public static boolean addDocument(RestHighLevelClient esClient, String indexName, Document document,
                                      List<String> fieldList, String primaryField, boolean createOnDuplicated) {
        String docJSONString = LuceneUtils.document2JSONString(document, fieldList);
        String id = document.get(primaryField);
        return addDocument(esClient, indexName, docJSONString, id, createOnDuplicated);
    }

    /**
     * 添加单个Document至Elasticsearch Index中
     * @param esClient
     * @param indexName
     * @param docJSONString
     * @param createOnDuplicated
     * @return
     */
    public static boolean addDocument(RestHighLevelClient esClient, String indexName, String docJSONString,
                                      String id, boolean createOnDuplicated) {
        try {
            IndexRequest indexRequest = new IndexRequest(indexName).create(createOnDuplicated)
                    .id(id).source(docJSONString, XContentType.JSON);
            IndexResponse response = esClient.index(indexRequest, RequestOptions.DEFAULT);
            RestStatus restStatus = response.status();
            return RestStatus.OK.equals(restStatus.getStatus());
        } catch (IOException e) {
            log.error("Add document:[{}] to index:[{}] occur exception.", docJSONString, indexName, e);
        }
        return false;
    }

    /**
     * 批量往指定索引中添加Document
     * @param esClient
     * @param indexName
     * @param docJSONStringList
     * @param batchSize
     * @param primaryField
     * @param createOnDuplicated
     * @return
     */
    public static boolean bulkAddDocuments(RestHighLevelClient esClient, String indexName,
                                           List<String> docJSONStringList, int batchSize,
                                           String primaryField, boolean createOnDuplicated) {
        Iterator<String> documentIterator = docJSONStringList.iterator();
        boolean isSuccess = true;
        try {
            int cursor = 0;
            BulkRequest bulkRequest = new BulkRequest();
            while (documentIterator.hasNext()) {
                String docJSONString = documentIterator.next();
                if (StringUtils.isNotEmpty(docJSONString)) {
                    Map<String, String> docMap = GsonUtils.getInstance().jsonStr2Map(docJSONString);
                    String id = docMap.get(primaryField);
                    if (cursor < batchSize) {
                        bulkRequest.add(new IndexRequest(indexName).create(createOnDuplicated)
                                .id(id).source(docJSONString, XContentType.JSON));
                    } else {
                        BulkResponse response = esClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                        boolean hasFailure = response.hasFailures();
                        if(hasFailure) {
                            isSuccess = false;
                            log.error("Bulk add Document:[{}] to Index:[{}] occur exception", batchSize, indexName);
                        } else {
                            cursor = 0;
                            bulkRequest = new BulkRequest();
                            bulkRequest.add(new IndexRequest(indexName).create(createOnDuplicated)
                                    .id(id).source(docJSONString, XContentType.JSON));
                        }
                    }
                    cursor++;
                } else {
                    log.warn("document is null or empty, can't be index to es.");
                }
            }
            //如果有剩余的Document，则最后提交一次
            int numberOfActions = bulkRequest.numberOfActions();
            if(numberOfActions > 0) {
                BulkResponse response = esClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                boolean hasFailure = response.hasFailures();
                if(hasFailure) {
                    isSuccess = false;
                    log.error("Bulk add Document:[{}] to Index:[{}] occur exception", batchSize, indexName);
                }
            }
        } catch (IOException e) {
            log.info("Bulk add Document to Index of Elasticsearch Server occur exception,Cause by: {}.", e.getMessage());
        }
        return isSuccess;
    }

    /**
     * 批量往指定索引中添加Document
     * @param esClient
     * @param indexName
     * @param documentList
     * @param batchSize
     * @return
     */
    public static boolean bulkAddDocuments(RestHighLevelClient esClient, String indexName,
                                           List<Document> documentList, int batchSize,
                                           List<String> fieldList, String primaryField,
                                           boolean createOnDuplicated) {
        Iterator<Document> documentIterator = documentList.iterator();
        boolean isSuccess = true;
        try {
            int cursor = 0;
            BulkRequest bulkRequest = new BulkRequest();
            while (documentIterator.hasNext()) {
                Document document = documentIterator.next();
                if (null != document) {
                    String id = document.get(primaryField);
                    String docJson = LuceneUtils.document2JSONString(document, fieldList);
                    if (cursor < batchSize) {
                        bulkRequest.add(new IndexRequest(indexName).create(createOnDuplicated)
                                .id(id).source(docJson, XContentType.JSON));
                    } else {
                        BulkResponse response = esClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                        boolean hasFailure = response.hasFailures();
                        if(hasFailure) {
                            isSuccess = false;
                            log.error("Bulk add Document:[{}] to Index:[{}] occur exception", batchSize, indexName);
                        } else {
                            cursor = 0;
                            bulkRequest = new BulkRequest();
                            bulkRequest.add(new IndexRequest(indexName).create(createOnDuplicated)
                                    .id(id).source(docJson, XContentType.JSON));
                        }
                    }
                    cursor++;
                } else {
                    log.warn("document is null or empty, can't be index to es.");
                }
            }
        } catch (IOException e) {
            log.info("Bulk add Document to Index of Elasticsearch Server occur exception,Cause by: {}.", e.getMessage());
        }
        return isSuccess;
    }
}
