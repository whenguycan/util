package com.kaltsit.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 简易json格式化工具
 * @Author wangcy
 * @Date 2021/6/10 9:24
 */
public class JsonUtils {

    public static String format(String jsonStr) {
        if(StringUtils.isEmpty(jsonStr)) {
            throw new RuntimeException("jsonStr 为空");
        }
        jsonStr = jsonStr.trim();
        char[] arr = jsonStr.toCharArray();
        StringBuilder sb = new StringBuilder();
        StringBuilder appender = new StringBuilder("\n");
        String spaces = "    ";
        for (char c : arr) {
            if(c == '{') {
                sb.append(c).append(appender.append(spaces));
            }else if(c == '}') {
                sb.append(appender.delete(appender.length() - spaces.length(), appender.length())).append(c);
            }else if(c == ',') {
                sb.append(c).append(appender);
            }else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
