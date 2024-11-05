package com.yida.datamigration.elasticsearch;

/**
 * Elasticsearch Field的数据类型
 */
public enum ElasticsearchFieldType {
    KEYWORD("keyword"),
    TEXT("text"),
    LONG("long"),
    INTEGER("integer"),
    SHORT("short"),
    BYTE("byte"),
    DOUBLE("double"),
    FLOAT("float"),
    HALF_FLOAT("half_float"),
    SCALED_FLOAT("scaled_float"),
    BOOLEAN("boolean"),
    DATE("date"),
    DATE_NANOS("date_nanos"),
    BINARY("binary"),
    INTEGER_RANGE("integer_range"),
    FLOAT_RANGE("float_range"),
    LONG_RANGE("long_range"),
    DOUBLE_RANGE("double_range"),
    DATE_RANGE("date_range");

    private String name;

    public static final String FIELD_TYPE_KEY_KEYWORD = "keyword";
    public static final String FIELD_TYPE_KEY_TEXT = "text";
    public static final String FIELD_TYPE_KEY_LONG = "long";
    public static final String FIELD_TYPE_KEY_INTEGER = "integer";
    public static final String FIELD_TYPE_KEY_SHORT = "short";
    public static final String FIELD_TYPE_KEY_BYTE = "byte";
    public static final String FIELD_TYPE_KEY_DOUBLE = "double";
    public static final String FIELD_TYPE_KEY_FLOAT = "float";
    public static final String FIELD_TYPE_KEY_HALF_FLOAT = "half_float";
    public static final String FIELD_TYPE_KEY_SCALED_FLOAT = "scaled_float";
    public static final String FIELD_TYPE_KEY_BOOLEAN = "boolean";
    public static final String FIELD_TYPE_KEY_DATE = "date";
    public static final String FIELD_TYPE_KEY_DATE_NANOS = "date_nanos";
    public static final String FIELD_TYPE_KEY_BINARY = "binary";
    public static final String FIELD_TYPE_KEY_INTEGER_RANGE = "integer_range";
    public static final String FIELD_TYPE_KEY_FLOAT_RANGE = "float_range";
    public static final String FIELD_TYPE_KEY_LONG_RANGE = "long_range";
    public static final String FIELD_TYPE_KEY_DOUBLE_RANGE = "double_range";
    public static final String FIELD_TYPE_KEY_DATE_RANGE = "date_range";

    public static ElasticsearchFieldType of(String fieldTypeKey) {
        ElasticsearchFieldType elasticsearchFieldType = null;
        if(ElasticsearchFieldType.KEYWORD.equals(fieldTypeKey)) {
            elasticsearchFieldType = KEYWORD;
        } else if(ElasticsearchFieldType.TEXT.equals(fieldTypeKey)) {
            elasticsearchFieldType = TEXT;
        } else if(ElasticsearchFieldType.LONG.equals(fieldTypeKey)) {
            elasticsearchFieldType = LONG;
        } else if(ElasticsearchFieldType.INTEGER.equals(fieldTypeKey)) {
            elasticsearchFieldType = INTEGER;
        } else if(ElasticsearchFieldType.SHORT.equals(fieldTypeKey)) {
            elasticsearchFieldType = SHORT;
        } else if(ElasticsearchFieldType.BYTE.equals(fieldTypeKey)) {
            elasticsearchFieldType = BYTE;
        } else if(ElasticsearchFieldType.DOUBLE.equals(fieldTypeKey)) {
            elasticsearchFieldType = DOUBLE;
        } else if(ElasticsearchFieldType.FLOAT.equals(fieldTypeKey)) {
            elasticsearchFieldType = FLOAT;
        } else if(ElasticsearchFieldType.HALF_FLOAT.equals(fieldTypeKey)) {
            elasticsearchFieldType = HALF_FLOAT;
        } else if(ElasticsearchFieldType.SCALED_FLOAT.equals(fieldTypeKey)) {
            elasticsearchFieldType = SCALED_FLOAT;
        } else if(ElasticsearchFieldType.BOOLEAN.equals(fieldTypeKey)) {
            elasticsearchFieldType = BOOLEAN;
        } else if(ElasticsearchFieldType.DATE.equals(fieldTypeKey)) {
            elasticsearchFieldType = DATE;
        } else if(ElasticsearchFieldType.DATE_NANOS.equals(fieldTypeKey)) {
            elasticsearchFieldType = DATE_NANOS;
        } else if(ElasticsearchFieldType.BINARY.equals(fieldTypeKey)) {
            elasticsearchFieldType = BINARY;
        } else if(ElasticsearchFieldType.INTEGER_RANGE.equals(fieldTypeKey)) {
            elasticsearchFieldType = INTEGER_RANGE;
        } else if(ElasticsearchFieldType.FLOAT_RANGE.equals(fieldTypeKey)) {
            elasticsearchFieldType = FLOAT_RANGE;
        } else if(ElasticsearchFieldType.LONG_RANGE.equals(fieldTypeKey)) {
            elasticsearchFieldType = LONG_RANGE;
        } else if(ElasticsearchFieldType.DOUBLE_RANGE.equals(fieldTypeKey)) {
            elasticsearchFieldType = DOUBLE_RANGE;
        } else if(ElasticsearchFieldType.DATE_RANGE.equals(fieldTypeKey)) {
            elasticsearchFieldType = DATE_RANGE;
        } else {
            throw new IllegalArgumentException("Unkown Elasticsearch FieldType Key, field-type-key:[" + fieldTypeKey);
        }
        return elasticsearchFieldType;
    }

    ElasticsearchFieldType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
