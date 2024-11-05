package com.yida.datamigration.listener;

import com.yida.datamigration.config.SystemConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartedListener implements ApplicationListener<ApplicationStartedEvent> {
    private static final Logger log = LoggerFactory.getLogger(ApplicationStartedListener.class);

    @Autowired
    private SystemConfig systemConfig;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        String appRootPath = systemConfig.getAppRootPath();
        String pidPath = systemConfig.getPidPath();
        log.info("appRootPath:[{}], pidPath:[{}]", appRootPath, pidPath);
    }
}
