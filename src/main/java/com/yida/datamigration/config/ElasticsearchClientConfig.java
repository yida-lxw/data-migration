package com.yida.datamigration.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix = "elasticsearch.server")
public class ElasticsearchClientConfig {
    private static final Logger log = LoggerFactory.getLogger(ElasticsearchClientConfig.class);

    /**Elasticsearch Server主机IP或域名*/
    @Value("${host}")
    private String host;

    /**Elasticsearch Server端口号*/
    @Value("${port}")
    private int port;

    /**Elasticsearch Client连接Server使用的协议*/
    @Value("${protocol}")
    private String protocol;

    /**Elasticsearch Server连接需要的账号*/
    @Value("${username}")
    private String username;

    /**Elasticsearch Server连接需要的密码*/
    @Value("${password}")
    private String password;

    private HttpHost[] getHttpHosts(String clientIps, int esHttpPort, String protocol) {
        String[] clientIpList = clientIps.split(",");
        HttpHost[] httpHosts = new HttpHost[clientIpList.length];
        for (int i = 0; i < clientIpList.length; i++) {
            httpHosts[i] = new HttpHost(clientIpList[i], esHttpPort, protocol);
        }
        return httpHosts;
    }

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        RestClientBuilder restClientBuilder = RestClient.builder(getHttpHosts(host, port, protocol));
        if(null == username || "".equals(username) || null == password || "".equals(password)) {
            return new RestHighLevelClient(restClientBuilder);
        }
        log.info("Start to initialize the Elasticsearch RestHighLevelClient(host:[{}],port:[{}])...", host, port);
        return new RestHighLevelClient(restClientBuilder.setHttpClientConfigCallback((HttpAsyncClientBuilder httpAsyncClientBuilder)
                -> httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider)));
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
