package com.yida.datamigration.utils;

import org.elasticsearch.common.io.stream.ByteBufferStreamInput;
import org.elasticsearch.common.io.stream.StreamInput;

import java.nio.ByteBuffer;

/**
 * 字符串操作工具类
 */
public class StringUtils {
    /**
     * 判断指定字符串是否为null或空字符串
     * @param source
     * @return
     */
    public static boolean isEmpty(String source) {
        return (null == source || "".equals(source));
    }

    /**
     * 判断指定字符串是否不为null且不为空字符串
     * @param source
     * @return
     */
    public static boolean isNotEmpty(String source) {
        return !isEmpty(source);
    }

    /**
     * 替换路径中的反斜杠为斜杠
     * @param appRootPath
     */
    public static String replaceBackSlash(String appRootPath) {
        if(null != appRootPath && !"".equals(appRootPath)) {
            appRootPath = appRootPath.replace("\\", "/");
        }
        return appRootPath;
    }

    /**
     * 删除字符串中包含的所有空格
     * @param source
     * @return
     */
    public static String removeAllWhiteSpace(String source) {
        if(isEmpty(source)) {
            return source;
        }
        return org.apache.commons.lang3.StringUtils.deleteWhitespace(source);
    }

    /**
     * 字符串转成ByteBuffer
     * @param source
     * @return
     */
    public static ByteBuffer string2ByteBuffer(String source) {
        return ByteBuffer.wrap(source.getBytes());
    }

    /**
     * 将字符串转成StreamInput对象
     * @param source
     * @return
     */
    public static StreamInput string2StreamInput(String source) {
        return new ByteBufferStreamInput(string2ByteBuffer(source));
    }
}
