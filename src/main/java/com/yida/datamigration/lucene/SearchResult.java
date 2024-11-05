package com.yida.datamigration.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;

import java.util.List;

/**
 * 查询返回的结果集
 */
public class SearchResult {
    /**当前页码(从1开始计算)*/
    private int currentPage;

    /**每页大小*/
    private int pageSize;

    /**上一页查询返回的最后一个文档,用于下一页查询*/
    private ScoreDoc lastScoreDoc;

    /**上一页查询返回的最后一个文档的docID*/
    private int lastScoreDocId;

    /**当前分页查询返回的文档列表*/
    private List<Document> documentList;

    private List<ScoreDoc> scoreDocList;

    /**当前分页查询使用的排序方式*/
    private Sort sort;

    public SearchResult() {}

    public SearchResult(int currentPage, int pageSize, ScoreDoc lastScoreDoc,
                        List<Document> documentList) {
        this(currentPage, pageSize, lastScoreDoc, -1, documentList, null);
    }

    public SearchResult(int currentPage, int pageSize, ScoreDoc lastScoreDoc,
                        List<Document> documentList, List<ScoreDoc> scoreDocList) {
        this(currentPage, pageSize, lastScoreDoc, -1, documentList, scoreDocList, null);
    }

    public SearchResult(int currentPage, int pageSize, ScoreDoc lastScoreDoc, int lastScoreDocId,
                        List<Document> documentList, List<ScoreDoc> scoreDocList) {
        this(currentPage, pageSize, lastScoreDoc, lastScoreDocId, documentList, scoreDocList, null);
    }

    public SearchResult(int currentPage, int pageSize, ScoreDoc lastScoreDoc, int lastScoreDocId,
                        List<Document> documentList, List<ScoreDoc> scoreDocList, Sort sort) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.lastScoreDoc = lastScoreDoc;
        this.lastScoreDocId = lastScoreDocId;
        if(this.lastScoreDocId <= 0 && null != this.lastScoreDoc) {
            this.lastScoreDocId = this.lastScoreDoc.doc;
        }
        this.documentList = documentList;
        this.scoreDocList = scoreDocList;
        this.sort = sort;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public ScoreDoc getLastScoreDoc() {
        return lastScoreDoc;
    }

    public void setLastScoreDoc(ScoreDoc lastScoreDoc) {
        this.lastScoreDoc = lastScoreDoc;
    }

    public int getLastScoreDocId() {
        return lastScoreDocId;
    }

    public void setLastScoreDocId(int lastScoreDocId) {
        this.lastScoreDocId = lastScoreDocId;
    }

    public List<Document> getDocumentList() {
        return documentList;
    }

    public void setDocumentList(List<Document> documentList) {
        this.documentList = documentList;
    }

    public List<ScoreDoc> getScoreDocList() {
        return scoreDocList;
    }

    public void setScoreDocList(List<ScoreDoc> scoreDocList) {
        this.scoreDocList = scoreDocList;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }
}
