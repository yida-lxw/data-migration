package com.yida.datamigration.service;


import com.yida.datamigration.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ApplicationRunnerStartService implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(ApplicationRunnerStartService.class);

    private static final String PIDFILE_PROPERTY_NAME = "PIDFILE";

    private static final String PID_FILE_PATH_KEY = "app.pid-path";

    @Autowired
    private Environment environment;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 从application.yml配置文件中获取进程 pid file 的路径
        String pidFilePath = environment.getProperty(PID_FILE_PATH_KEY);
        if (pidFilePath != null && !pidFilePath.isEmpty()) {
            pidFilePath = StringUtils.replaceBackSlash(pidFilePath);
            System.setProperty(PIDFILE_PROPERTY_NAME, pidFilePath);
        }
    }
}

