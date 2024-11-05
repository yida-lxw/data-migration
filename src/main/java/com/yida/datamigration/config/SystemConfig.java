package com.yida.datamigration.config;

import com.yida.datamigration.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SystemConfig {
    /**当前项目的根目录*/
    @Value("${app.root-path}")
    private String appRootPath;

    /**当前项目的进程pid路径*/
    @Value("${app.pid-path}")
    private String pidPath;

    public String getAppRootPath() {
        appRootPath = StringUtils.replaceBackSlash(appRootPath);
        return appRootPath;
    }

    public String getPidPath() {
        pidPath = StringUtils.replaceBackSlash(pidPath);
        return pidPath;
    }
}
