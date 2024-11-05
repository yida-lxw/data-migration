package com.yida.datamigration.log;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class Log4JConfig {
    @PostConstruct
    public void init() {
        String rootPath = System.getProperty("user.dir");
        String log4jBaseDir = rootPath + "/logs/";
        System.setProperty("log4jBaseDir", log4jBaseDir);
    }
}
