package com.yida.datamigration.core;

import com.yida.datamigration.config.LuceneConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class SystemTerminateHook {
    private static final Logger log = LoggerFactory.getLogger(SystemTerminateHook.class);

    @Autowired
    private LuceneConfig luceneConfig;

    @PreDestroy
    public void preDestroy() {
        log.info("System begin to be destroyed.");
        luceneConfig.closeIndexSearcher();
    }
}
