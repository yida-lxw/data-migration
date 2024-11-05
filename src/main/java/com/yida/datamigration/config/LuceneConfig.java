package com.yida.datamigration.config;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

/**
 * Lucene配置类
 */
@Configuration
@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix = "lucene.index-read")
public class LuceneConfig {
    private static final Logger log = LoggerFactory.getLogger(LuceneConfig.class);

    /**索引数据根目录*/
    @Value("${index-basedir}")
    private String indexBaseDir;

    /**Lucene版本号*/
    @Value("${version}")
    private String luceneVersion;

    /**是否开启近实时Reader*/
    @Value("${enable-NRTReader}")
    private boolean enableNRTReader;

    /**一个批次从Lucene读取的索引数据条数*/
    @Value("${page-size}")
    private int pageSize;


    private Directory directory;

    /**Lucene索引读取器*/
    private IndexReader indexReader;

    private IndexSearcher indexSearcher;

    @Bean
    @DependsOn({"indexReader", "luceneIndexReadThreadPoolExecutor"})
    public IndexSearcher indexSearcher(IndexReader indexReader, ExecutorService luceneIndexReadThreadPoolExecutor) {
        indexSearcher = new IndexSearcher(indexReader, luceneIndexReadThreadPoolExecutor);
        return indexSearcher;
    }

    @Bean
    @DependsOn("directory")
    public IndexReader indexReader(Directory directory) {
        try {
            if(null == indexReader){
                indexReader = DirectoryReader.open(directory);
            } else {
                if(enableNRTReader && indexReader instanceof DirectoryReader) {
                    //开启近实时Reader,能立即看到动态添加/删除的索引变化
                    indexReader = DirectoryReader.openIfChanged((DirectoryReader)indexReader);
                }
            }
        } catch (IOException e) {
            log.error("Open Index Reader occur exception:{}", e.getMessage());
        }
        return indexReader;
    }

    @Bean
    public Directory directory() {
        try {
            directory = FSDirectory.open(new File(indexBaseDir).toPath());
        } catch (IOException e) {
            log.error("Open Index directory:[{}] occur exception:{}",
                    (null == indexBaseDir || "".equals(indexBaseDir))? "null" : indexBaseDir, e.getMessage());
        }
        return directory;
    }
    @Bean
    public Version version() {
        String[] versionArray = luceneVersion.split("\\.");
        return Version.fromBits(Integer.parseInt(versionArray[0]), Integer.parseInt(versionArray[1]),
                Integer.parseInt(versionArray[2]));
    }

    /**
     * 关闭IndexSearcher实例
     */
    public void closeIndexSearcher() {
        if (null != indexSearcher) {
            try {
                indexSearcher.getIndexReader().close();
                log.info("IndexSearcher had been closed.");
            } catch (IOException e) {
                log.error("while closing the IndexSearcher instance, an exception occurred.");
            }
        }
    }

    public String getIndexBaseDir() {
        return indexBaseDir;
    }

    public String getLuceneVersion() {
        return luceneVersion;
    }

    public boolean isEnableNRTReader() {
        return enableNRTReader;
    }

    public int getPageSize() {
        return pageSize;
    }

    public Directory getDirectory() {
        return directory;
    }

    public IndexReader getIndexReader() {
        return indexReader;
    }

    public IndexSearcher getIndexSearcher() {
        return indexSearcher;
    }
}
