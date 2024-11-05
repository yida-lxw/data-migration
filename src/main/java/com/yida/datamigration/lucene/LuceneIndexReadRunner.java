package com.yida.datamigration.lucene;

import com.yida.datamigration.config.ElasticsearchIndexConfig;
import com.yida.datamigration.config.LuceneConfig;
import com.yida.datamigration.service.LuceneIndexMigration;
import org.apache.lucene.search.IndexSearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Spring Boot程序启动时就开始执行Lucene索引数据读取并迁移操作
 */
@Component
public class LuceneIndexReadRunner implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(LuceneIndexReadRunner.class);

    @Autowired
    private IndexSearcher indexSearcher;

    @Autowired
    private LuceneConfig luceneConfig;

    @Autowired
    private ElasticsearchIndexConfig elasticsearchIndexConfig;

    @Autowired
    private LuceneIndexMigration luceneIndexMigration;

    @Override
    public void run(String... args) throws Exception {
        long start = System.currentTimeMillis();
        boolean feedback = luceneIndexMigration.doMigration(luceneConfig, elasticsearchIndexConfig, indexSearcher);
        if (feedback) {
            log.info("Lucene index data at indexDir:[{}] migrated to Elasticsearch index:[{}] successfully",
                    luceneConfig.getIndexBaseDir(), elasticsearchIndexConfig.getIndexName());
        }
        long end = System.currentTimeMillis();
        long taken = end - start;
        log.info("It had taken [{}] ms to migrate Lucene index data to the index:[{}] of Elasticsearch.", taken,
                elasticsearchIndexConfig.getIndexName());
    }
}
