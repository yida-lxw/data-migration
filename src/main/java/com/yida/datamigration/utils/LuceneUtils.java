package com.yida.datamigration.utils;

import com.yida.datamigration.lucene.SearchResult;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Lucene工具类
 */
public class LuceneUtils {
    private static final Logger log = LoggerFactory.getLogger(LuceneUtils.class);

    /**默认分页查询的每页大小：10*/
    private static final int DEFAULT_PAGESIZE = 10;

    private static volatile TopDocs topDocs;

    /**
     * 关闭IndexReader
     * @param reader
     */
    public static void closeIndexReader(IndexReader reader) {
        if (null != reader) {
            try {
                reader.close();
                reader = null;
            } catch (IOException e) {
                log.error("Close IndexReader occur exception:{}", e.getMessage());
            }
        }
    }

    /**
     * 返回已经删除尚未提交的文档总数[注意：请自己手动关闭IndexReader]
     *
     * @param reader
     * @return
     */
    public static int getDeletedDocNum(IndexReader reader) {
        return getMaxDocId(reader) - getIndexTotalCount(reader);
    }

    /**
     * 返回索引文档中最大文档ID[注意：请自己手动关闭IndexReader]
     *
     * @param reader
     * @return
     */
    public static int getMaxDocId(IndexReader reader) {
        return reader.maxDoc();
    }

    /**
     * 返回索引文档的总数[注意：请自己手动关闭IndexReader]
     * @param reader
     * @return
     */
    public static int getIndexTotalCount(IndexReader reader) {
        return reader.numDocs();
    }

    /**
     * 根据docId查询索引文档
     *
     * @param reader IndexReader对象
     * @param docID  documentId
     * @return
     */
    public static Document findDocumentByDocId(IndexReader reader, int docID) {
        return findDocumentByDocId(reader, docID, null);
    }


    /**
     * 根据docId查询索引文档
     * @param reader       IndexReader对象
     * @param docID        documentId
     * @param fieldsToLoad 需要返回的field
     * @return
     */
    public static Document findDocumentByDocId(IndexReader reader, int docID, Set<String> fieldsToLoad) {
        try {
            return reader.document(docID, fieldsToLoad);
        } catch (IOException e) {
            log.error("Invoking findDocumentByDocId({}) to Query Index Document with IndexSearcher Instance occur exception:{}", docID, e.getMessage());
            return null;
        }
    }

    /**
     * 索引文档分页查询
     * @param searcher
     * @param query
     * @param currentPage  当前页码(从1开始计算)
     * @param pageSize     每页大小
     * @return
     */
    public static SearchResult pageQuery(IndexSearcher searcher, Query query, int currentPage, int pageSize) {
        return pageQuery(searcher, query, null, currentPage, pageSize);
    }

    /**
     * 索引文档分页查询
     * @param searcher
     * @param query
     * @param sort  排序方式
     * @param currentPage  当前页码(从1开始计算)
     * @param pageSize     每页大小
     * @return
     */
    public static SearchResult pageQuery(IndexSearcher searcher, Query query, Sort sort,
                                         int currentPage, int pageSize) {
        return pageQuery(searcher, query, sort, currentPage, pageSize, false);
    }


    /**
     * 索引文档分页查询
     * @param searcher
     * @param query
     * @param sort  排序方式
     * @param currentPage  当前页码(从1开始计算)
     * @param pageSize     每页大小
     * @param doDocScores     是否返回每个Document的评分
     * @return
     */
    public static SearchResult pageQuery(IndexSearcher searcher, Query query, Sort sort,
                                     int currentPage, int pageSize, boolean doDocScores) {
        if(currentPage <= 0) {
            currentPage = 1;
        }
        int totalDocsCount = getIndexTotalCount(searcher.getIndexReader());
        if(totalDocsCount <= 0) {
            return new SearchResult();
        }
        if(topDocs == null) {
            topDocs = queryTopDocs(searcher, query, sort, totalDocsCount, doDocScores);
        }
        if(null == topDocs || null == topDocs.scoreDocs) {
            return new SearchResult();
        }
        return buildSearchResult(topDocs, currentPage, pageSize, sort, searcher);
    }


    /**
     * 索引文档分页查询
     * @param lastScoreDoc  上一次查询的最后一个文档
     * @param searcher
     * @param query
     * @return
     */
    public static SearchResult searchAfter(ScoreDoc lastScoreDoc, IndexSearcher searcher, Query query, int currentPage) {
        return searchAfter(lastScoreDoc, searcher, query, (Sort)null, currentPage);
    }

    /**
     * 索引文档分页查询
     * @param lastScoreDoc  上一次查询的最后一个文档
     * @param searcher
     * @param query
     * @return
     */
    public static SearchResult searchAfter(ScoreDoc lastScoreDoc, IndexSearcher searcher, Query query, Sort sort, int currentPage) {
        return searchAfter(lastScoreDoc, searcher, query, sort, currentPage, DEFAULT_PAGESIZE);
    }

    /**
     * 索引文档分页查询
     * @param lastScoreDoc  上一次查询的最后一个文档
     * @param searcher
     * @param query
     * @param pageSize     每页大小
     * @return
     */
    public static SearchResult searchAfter(ScoreDoc lastScoreDoc, IndexSearcher searcher, Query query, Sort sort,
                                           int currentPage, int pageSize) {
        return searchAfter(lastScoreDoc, searcher, query, sort, currentPage, pageSize, false);
    }

    /**
     * 索引文档分页查询
     * @param lastScoreDoc  上一次查询的最后一个文档
     * @param searcher
     * @param query
     * @param currentPage   当前页码(从1开始计算)
     * @param pageSize     每页大小
     * @param doDocScores     是否返回每个Document的评分
     * @return
     */
    public static SearchResult searchAfter(ScoreDoc lastScoreDoc, IndexSearcher searcher, Query query, Sort sort,
                                           int currentPage, int pageSize, boolean doDocScores) {
        SearchResult searchResult = null;
        try {
            sort = buildDefaultSort(sort);
            if(topDocs == null) {
                topDocs = searcher.searchAfter(lastScoreDoc, query, pageSize, sort, doDocScores);
            }
            searchResult = buildSearchResult(searcher, sort, currentPage, pageSize, topDocs);
        } catch (IOException e) {
            log.error("Using searchAfter() to Query Index Document with IndexSearcher Instance occur exception:{}", e.getMessage());
        }
        return searchResult;
    }

    /**
     * 索引文档查询
     * @param searcher
     * @param query
     * @return
     */
    public static SearchResult query(IndexSearcher searcher, Query query) {
        return query(searcher, query, (Sort)null);
    }

    /**
     * 索引文档查询
     * @param searcher
     * @param query
     * @param sort  排序方式
     * @return
     */
    public static SearchResult query(IndexSearcher searcher, Query query, Sort sort) {
        return query(searcher, query, sort, DEFAULT_PAGESIZE);
    }

    /**
     * 索引文档查询
     * @param searcher
     * @param query
     * @param sort  排序方式
     * @param topN  返回前N条
     * @return
     */
    public static SearchResult query(IndexSearcher searcher, Query query, Sort sort, int topN) {
        return query(searcher, query, sort, topN, false);
    }

    /**
     * 索引文档查询
     * @param searcher
     * @param query
     * @param sort  排序方式
     * @param topN  返回前N条
     * @param doDocScores     是否返回每个Document的评分
     * @return
     */
    public static SearchResult query(IndexSearcher searcher, Query query, Sort sort,
                                     int topN, boolean doDocScores) {
        SearchResult searchResult = null;
        try {
            sort = buildDefaultSort(sort);
            if(null == topDocs) {
                topDocs = searcher.search(query, topN, sort, doDocScores);
            }
            searchResult = buildSearchResult(searcher, sort, 1, topN, topDocs);
        } catch (IOException e) {
            log.error("Query Index Document with IndexSearcher Instance occur exception:{}", e.getMessage());
        }
        return searchResult;
    }

    /**
     * 索引文档查询
     * @param searcher
     * @param query
     * @param sort  排序方式
     * @param topN  返回前N条
     * @param doDocScores     是否返回每个Document的评分
     * @return
     */
    public static TopDocs queryTopDocs(IndexSearcher searcher, Query query, Sort sort,
                                     int topN, boolean doDocScores) {
        TopDocs topDocs = null;
        try {
            sort = buildDefaultSort(sort);
            return searcher.search(query, topN, sort, doDocScores);
        } catch (IOException e) {
            log.error("Query TopN Index Document with IndexSearcher Instance occur exception:{}", e.getMessage());
        }
        return topDocs;
    }

    /**
     * @param query            索引查询对象
     * @param prefix           高亮前缀字符串
     * @param stuffix          高亮后缀字符串
     * @param fragmenterLength 摘要最大长度
     * @return
     * @Title: createHighlighter
     * @Description: 创建高亮器
     */
    public static Highlighter createHighlighter(Query query, String prefix, String stuffix, int fragmenterLength) {
        Formatter formatter = new SimpleHTMLFormatter((prefix == null || prefix.trim().length() == 0) ?
                "<font color=\"red\">" : prefix, (stuffix == null || stuffix.trim().length() == 0) ? "</font>" : stuffix);
        Scorer fragmentScorer = new QueryScorer(query);
        Highlighter highlighter = new Highlighter(formatter, fragmentScorer);
        Fragmenter fragmenter = new SimpleFragmenter(fragmenterLength <= 0 ? 50 : fragmenterLength);
        highlighter.setTextFragmenter(fragmenter);
        return highlighter;
    }

    /**
     * @param document    索引文档对象
     * @param highlighter 高亮器
     * @param analyzer    索引分词器
     * @param field       高亮字段
     * @return
     * @throws IOException
     * @throws InvalidTokenOffsetsException
     * @Title: highlight
     * @Description: 生成高亮文本
     */
    public static String highlight(Document document, Highlighter highlighter, Analyzer analyzer, String field) throws IOException {
        List<IndexableField> list = document.getFields();
        for (IndexableField fieldable : list) {
            String fieldValue = fieldable.stringValue();
            if (fieldable.name().equals(field)) {
                try {
                    fieldValue = highlighter.getBestFragment(analyzer, field, fieldValue);
                } catch (InvalidTokenOffsetsException e) {
                    fieldValue = fieldable.stringValue();
                }
                return (fieldValue == null || fieldValue.trim().length() == 0) ? fieldable.stringValue() : fieldValue;
            }
        }
        return null;
    }

    /**
     * 获取符合条件的总记录数
     *
     * @param search 索引查下
     * @param query 查询条件
     * @return 总记录数
     */
    public static int searchTotalRecord(IndexSearcher search, Query query) {
        ScoreDoc[] docs = null;
        try {
            TopDocs topDocs = search.search(query, Integer.MAX_VALUE);
            if (topDocs == null || topDocs.scoreDocs == null || topDocs.scoreDocs.length == 0) {
                return 0;
            }
            docs = topDocs.scoreDocs;
        } catch (IOException e) {
            log.error("Invoking searchTotalRecord() to query matched record count occur exception:{}", e.getMessage());
            return -1;
        }
        return docs.length;
    }

    /**
     * 构建SearchResult
     * @param searcher
     * @param sort
     * @param currentPage
     * @param pageSize
     * @param topDocs
     * @return
     */
    private static SearchResult buildSearchResult(IndexSearcher searcher, Sort sort, int currentPage, int pageSize,
                                                  TopDocs topDocs) {
        List<Document> documentList;
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        List<ScoreDoc> scoreDocList = (null == scoreDocs || scoreDocs.length <= 0)?
                new ArrayList<>() : Arrays.asList(scoreDocs);

        documentList = buildDocuments(scoreDocs, searcher);
        boolean isEmptyDocs = (null == scoreDocs || scoreDocs.length <= 0);
        ScoreDoc lastScoreDoc = (isEmptyDocs)? null : scoreDocs[scoreDocs.length - 1];
        int lastScoreDocId = (null == lastScoreDoc)? -1 : lastScoreDoc.doc;
        return new SearchResult(currentPage, pageSize, lastScoreDoc, lastScoreDocId, documentList, scoreDocList, sort);
    }

    private static SearchResult buildSearchResult(IndexSearcher searcher, Sort sort, int currentPage, int pageSize,
                                                  List<ScoreDoc> scoreDocList) {
        int scoreDocSize = (null == scoreDocList)? 0 : scoreDocList.size();
        boolean isEmptyDocs = (null == scoreDocList || scoreDocSize <= 0);
        ScoreDoc lastScoreDoc = (isEmptyDocs)? null : scoreDocList.get(scoreDocSize - 1);
        int lastScoreDocId = (null == lastScoreDoc)? -1 : lastScoreDoc.doc;
        List<Document> documentList = buildDocuments(scoreDocList, searcher);
        return new SearchResult(currentPage, pageSize, lastScoreDoc, lastScoreDocId, documentList, scoreDocList, sort);
    }

    private static SearchResult buildSearchResult(Sort sort, int currentPage, int pageSize,
                                                  List<ScoreDoc> scoreDocList, List<Document> documentList) {
        int scoreDocSize = (null == scoreDocList)? 0 : scoreDocList.size();
        boolean isEmptyDocs = (null == scoreDocList || scoreDocSize <= 0);
        ScoreDoc lastScoreDoc = (isEmptyDocs)? null : scoreDocList.get(scoreDocSize - 1);
        int lastScoreDocId = (null == lastScoreDoc)? -1 : lastScoreDoc.doc;
        return new SearchResult(currentPage, pageSize, lastScoreDoc, lastScoreDocId, documentList, scoreDocList, sort);
    }

    /**
     * 将ScoreDoc[]转成List<Document>
     * @param scoreList
     * @param searcher
     * @return
     */
    public static List<Document> buildDocuments(List<ScoreDoc> scoreList, IndexSearcher searcher) {
        int length = (null == scoreList)? 0 : scoreList.size();
        if (length <= 0) {
            return Collections.emptyList();
        }
        List<Document> docList = new ArrayList<>();
        try {
            for (int i = 0; i < length; i++) {
                Document doc = searcher.doc(scoreList.get(i).doc);
                docList.add(doc);
            }
        } catch (IOException e) {
            log.error("ScoreDoc convert to Document List occur exception:{}", e.getMessage());
        }
        return docList;
    }

    /**
     * 将ScoreDoc[]转成List<Document>
     * @param topDocs
     * @param searcher
     * @return
     */
    public static SearchResult buildSearchResult(TopDocs topDocs, int currentPage, int pageSize, Sort sort, IndexSearcher searcher) {
        int length = (null == topDocs || null == topDocs.scoreDocs)? 0 : topDocs.scoreDocs.length;
        if (length <= 0) {
            return new SearchResult();
        }

        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, Integer.valueOf(String.valueOf(topDocs.totalHits.value)));

        List<Document> documentList = new ArrayList<>();
        List<ScoreDoc> scoreDocList = new ArrayList<>();
        SearchResult searchResult = new SearchResult();
        try {
            for (int i = start; i < end; i++) {
                ScoreDoc scoreDoc = topDocs.scoreDocs[i];
                int docId = scoreDoc.doc;
                Document doc = searcher.doc(docId);
                documentList.add(doc);
                scoreDocList.add(scoreDoc);
            }
            searchResult = buildSearchResult(sort, currentPage, pageSize, scoreDocList, documentList);
        } catch (IOException e) {
            log.error("Invoking searcher.doc() occur exception", e);
        }
        return searchResult;
    }

    /**
     * 将ScoreDoc[]转成List<Document>
     * @param scores
     * @param searcher
     * @return
     */
    public static List<Document> buildDocuments(ScoreDoc[] scores, IndexSearcher searcher) {
        int length = (null == scores)? 0 : scores.length;
        if (length <= 0) {
            return Collections.emptyList();
        }
        List<Document> docList = new ArrayList<>();
        try {
            for (int i = 0; i < length; i++) {
                Document doc = searcher.doc(scores[i].doc);
                docList.add(doc);
            }
        } catch (IOException e) {
            log.error("ScoreDoc convert to Document List occur exception:{}", e.getMessage());
        }
        return docList;
    }

    /**
     * 构建默认的排序器
     * @return
     */
    private static Sort buildDefaultSort() {
        return buildDefaultSort(null);
    }

    /**
     * 构建默认的排序器
     * @param sort
     * @return
     */
    private static Sort buildDefaultSort(Sort sort) {
        if(null == sort) {
            //默认先按文档评分排序，再按文档ID倒序
            sort = new Sort(SortField.FIELD_SCORE, new SortField("id", SortField.Type.STRING, true));
        }
        return sort;
    }

    /**
     * 计算总页数
     * @param totalDocsCount  文档总数
     * @param pageSize         每页大小
     * @return
     */
    public static int calculateTotalPage(int totalDocsCount, int pageSize) {
        if(pageSize <= 0) {
            throw new IllegalArgumentException("pageSize:[" + pageSize + "] MUST BE NOT NULL.");
        }
        int remainder = totalDocsCount % pageSize;
        int quotient = totalDocsCount / pageSize;
        return (remainder % 2 == 0)? quotient : quotient + 1;
    }

    /**
     * 将Lucene Document转成Map
     * @param document
     * @param fieldList
     * @return
     */
    public static Map<String, String> document2Map(Document document, String[] fieldList) {
        Map<String, String> docMap = new TreeMap<>();
        for(String field : fieldList) {
            String fieldValue = document.get(field);
            docMap.put(field, fieldValue);
        }
        return docMap;
    }

    /**
     * 将单个Document转成JSON String
     * @param document
     * @param fieldList
     * @return
     */
    public static String document2JSONString(Document document, List<String> fieldList) {
        String docJSON = null;
        String[] fieldArray = fieldList.toArray(new String[] {});
        Map<String, String> docMap = document2Map(document, fieldArray);
        if(null == docMap || docMap.size() <= 0) {
            return docJSON;
        }
        return GsonUtils.getInstance().beanToString(docMap);
    }

    /**
     * 将List<Document>转成List<String>
     * @param documentList
     * @param fieldArray
     * @return
     */
    public static List<String> documents2JSONList(List<Document> documentList, String[] fieldArray) {
        List<String> docJSONList = new ArrayList<>();
        if(null != documentList && documentList.size() > 0) {
            for(Document document : documentList) {
                Map<String, String> docMap = document2Map(document, fieldArray);
                if(null == docMap || docMap.size() <= 0) {
                    continue;
                }
                String docJSON = GsonUtils.getInstance().beanToString(docMap);
                docJSONList.add(docJSON);
            }
        }
        return docJSONList;
    }

    /**
     * 将List<Document>转成List<String>
     * @param documentList
     * @param fieldList
     * @return
     */
    public static List<String> documents2JSONList(List<Document> documentList, List<String> fieldList) {
        String[] fieldNameArray = fieldList.toArray(new String[] {});
        return documents2JSONList(documentList, fieldNameArray);
    }

    /**
     * 将List<Document>转成List<Map<String, String>>
     * @param documentList
     * @param fieldList
     * @return
     */
    public static List<Map<String, String>> documents2ListMap(List<Document> documentList, List<String> fieldList) {
        String[] fieldArray = fieldList.toArray(new String[] {});
        return documents2ListMap(documentList, fieldArray);
    }

    /**
     * 将List<Document>转成List<Map<String, String>>
     * @param documentList
     * @param fieldArray
     * @return
     */
    public static List<Map<String, String>> documents2ListMap(List<Document> documentList, String[] fieldArray) {
        List<Map<String, String>> mapList = new ArrayList<>();
        if(null != documentList && documentList.size() > 0) {
            for(Document document : documentList) {
                Map<String, String> docMap = document2Map(document, fieldArray);
                if(null == docMap) {
                    continue;
                }
                mapList.add(docMap);
            }
        }
        return mapList;
    }
}
