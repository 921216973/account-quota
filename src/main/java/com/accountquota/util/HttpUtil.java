/**
 * 
 */
package com.accountquota.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class HttpUtil {
	public static final String UTF_8 = "UTF-8";
	private static final PooledHttpClientAdaptor adaptor = new PooledHttpClientAdaptor();
	
	public static String doGet(String apiUrl, Map<String, String> headers, Map<String, String> params) {
		return adaptor.doGet(apiUrl, headers, params);
	}
	public static String doPost(String apiUrl, Map<String, String> headers, Map<String, Object> params) {
		return adaptor.doPost(apiUrl, headers, params);
	}

	/**
	 * json格式post请求
	 * @param apiUrl
	 * @param headers
	 * @param params json字符串
	 * @return
	 */
	public static String doPostJson(String apiUrl, Map<String, String> headers, String params) {
		return adaptor.doPostJson(apiUrl, headers, params);
	}

	public static String doPostJson(String apiUrl, String params) {
		return adaptor.doPostJson(apiUrl, null, params);
	}


	public static String doDelete(String url, Map<String, String> headers, HashMap<String, Object> params) {
		return adaptor.doDelete(url, headers, params);
	}
	
	public static String getUrlWithParams(String url, Map<String, String> params) {
        boolean first = true;
        StringBuilder sb = new StringBuilder(url);
        for (String key : params.keySet()) {
            char ch = '&';
            if (first == true) {
                ch = '?';
                first = false;
            }
            String value = params.get(key);
            try {
                String sval = URLEncoder.encode(value, UTF_8);
                sb.append(ch).append(key).append("=").append(sval);
            } catch (UnsupportedEncodingException e) {
            }
        }
        return sb.toString();
    }
	
	public static Map<String, Object> convent2Map(Object b) {
		Map<String, Object> params = new HashMap<>();
		for(Field field: b.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			Object val = null;
			try {
				val = field.get(b);
			} catch (IllegalArgumentException | IllegalAccessException e) {
			}
			if(val != null) {
				params.put(field.getName(), val);
			}
		}
		return params;
	}

}
