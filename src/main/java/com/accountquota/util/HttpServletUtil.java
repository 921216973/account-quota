package com.accountquota.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class HttpServletUtil {
    /**
     * 以JSON格式输出
     *
     * @param response
     */
    public static void responseOutWithJson(HttpServletResponse response, Object responseObject) {
        // 将实体对象转换为JSON Object转换
        if (responseObject != null) {
            String s = JSON.toJSONString(responseObject, SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteNullStringAsEmpty);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            PrintWriter out = null;
            try {
                out = response.getWriter();
                out.append(s);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    out.close();
                }
            }
        }
    }
}
