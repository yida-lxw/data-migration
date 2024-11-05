package com.yida.datamigration.elasticsearch;

import java.io.Serializable;
import java.util.Map;

/**
 * Elasticsearch Index Mapping
 */
public class ElasticsearchIndexMapping implements Serializable {
    private static final long serialVersionUID = 610890087336170344L;

    private Map<String, ElasticsearchIndexFieldProperty> properties;

    public ElasticsearchIndexMapping() {}

    public ElasticsearchIndexMapping(Map<String, ElasticsearchIndexFieldProperty> properties) {
        this.properties = properties;
    }

    public Map<String, ElasticsearchIndexFieldProperty> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, ElasticsearchIndexFieldProperty> properties) {
        this.properties = properties;
    }
}
