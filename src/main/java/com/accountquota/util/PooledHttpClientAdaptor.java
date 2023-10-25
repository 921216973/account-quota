/**
 *
 */
package com.accountquota.util;


import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author
 *
 */
public class PooledHttpClientAdaptor {
    private static final Logger logger = LoggerFactory.getLogger(PooledHttpClientAdaptor.class);

    private static final int DEFAULT_POOL_MAX_TOTAL = 200;
    private static final int DEFAULT_POOL_MAX_PER_ROUTE = 200;

    private static final int DEFAULT_CONNECT_TIMEOUT = 10000;
    private static final int DEFAULT_CONNECT_REQUEST_TIMEOUT = 10000;
    private static final int DEFAULT_SOCKET_TIMEOUT = 10000;

    private PoolingHttpClientConnectionManager gcm = null;

    private CloseableHttpClient httpClient = null;

    private IdleConnectionMonitorThread idleThread = null;

    // 连接池的最大连接数
    private final int maxTotal;
    // 连接池按route配置的最大连接数
    private final int maxPerRoute;

    // tcp connect的超时时间
    private final int connectTimeout;
    // 从连接池获取连接的超时时间
    private final int connectRequestTimeout;
    // tcp io的读写超时时间
    private final int socketTimeout;

    public PooledHttpClientAdaptor() {
        this(
                PooledHttpClientAdaptor.DEFAULT_POOL_MAX_TOTAL,
                PooledHttpClientAdaptor.DEFAULT_POOL_MAX_PER_ROUTE,
                PooledHttpClientAdaptor.DEFAULT_CONNECT_TIMEOUT,
                PooledHttpClientAdaptor.DEFAULT_CONNECT_REQUEST_TIMEOUT,
                PooledHttpClientAdaptor.DEFAULT_SOCKET_TIMEOUT
        );
    }

    public PooledHttpClientAdaptor(int maxTotal, int maxPerRoute, int connectTimeout, int connectRequestTimeout, int socketTimeout) {
        this.maxTotal = maxTotal;
        this.maxPerRoute = maxPerRoute;
        this.connectTimeout = connectTimeout;
        this.connectRequestTimeout = connectRequestTimeout;
        this.socketTimeout = socketTimeout;

        final SSLConnectionSocketFactory sslsf;
        try {
            sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault(),
                    NoopHostnameVerifier.INSTANCE);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslsf)
                .build();

        this.gcm = new PoolingHttpClientConnectionManager(registry);
        this.gcm.setMaxTotal(this.maxTotal);
        this.gcm.setDefaultMaxPerRoute(this.maxPerRoute);

//        HttpHost proxy = new HttpHost("100.67.154.166", 50401, "http");
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(this.connectTimeout)                     // 设置连接超时
                .setSocketTimeout(this.socketTimeout)                       // 设置读取超时
                .setConnectionRequestTimeout(this.connectRequestTimeout)    // 设置从连接池获取连接实例的超时
//                .setProxy(proxy)
                .build();

        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClient = httpClientBuilder
                .setConnectionManager(this.gcm)
                .setDefaultRequestConfig(requestConfig)
                .build();

        idleThread = new IdleConnectionMonitorThread(this.gcm);
        idleThread.start();

    }

    public String doGet(String url) {
        return this.doGet(url, Collections.emptyMap(), Collections.emptyMap());
    }

    public String doGet(String url, Map<String, String> params) {
        return this.doGet(url, Collections.emptyMap(), params);
    }

    public String doGet(String url, Map<String, String> headers, Map<String, String> params) {

        String headerStr = null;
        if (headers != null) {
            headerStr = JsonUtil.convertObject2Json(headers);
        }
        logger.info("doGet url:" + url + ". headers :" + headerStr + ". params :" + JsonUtil.convertObject2Json(params));

        // *) 构建GET请求头
        String apiUrl = HttpUtil.getUrlWithParams(url, params);
        HttpGet httpGet = new HttpGet(apiUrl);

        // *) 设置header信息
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpGet.addHeader(entry.getKey(), entry.getValue());
            }
        }

        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            if (response == null || response.getStatusLine() == null) {
                return null;
            }

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity entityRes = response.getEntity();
                if (entityRes != null) {
                    String res = EntityUtils.toString(entityRes, HttpUtil.UTF_8);
                    logger.info(res);
                    return res;
                }
            }
            return null;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    public String doPost(String apiUrl, Map<String, Object> params) {
        return this.doPost(apiUrl, Collections.emptyMap(), params);
    }

    public String doPost(String apiUrl, Map<String, String> headers, Map<String, Object> params) {
        String headerStr = null;
        if (headers != null) {
            headerStr = JsonUtil.convertObject2Json(headers);
        }
        logger.info("doPost url:" + apiUrl + ". headers :" + headerStr + ". params :" + JsonUtil.convertObject2Json(params));
        HttpPost httpPost = new HttpPost(apiUrl);

        // 配置请求headers
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }
        }

        // 配置请求参数
        if (params != null && params.size() > 0) {
            //参数格式不匹配会报400 BadRequest
            HttpEntity entityReq = getUrlEncodedFormEntity(params);
            httpPost.setEntity(entityReq);
        }

        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            if (response == null || response.getStatusLine() == null) {
                return null;
            }

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity entityRes = response.getEntity();
                if (entityRes != null) {
                    String res = EntityUtils.toString(entityRes, HttpUtil.UTF_8);
                    logger.info(res);
                    System.out.println(res);
                    return res;
                }
            }
            return null;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    /**
     * 发送post请求，json格式
     * @param apiUrl
     * @param headers
     * @param params json格式
     * @return
     */
    public String doPostJson(String apiUrl, Map<String, String> headers, String params) {
//        String headerStr = null;
//        if (headers != null) {
//            headerStr = JsonUtil.convertObject2Json(headers);
//        }
//        logger.info("doPost url:" + apiUrl + ". headers :" + headerStr + ". params :" + params);
        HttpPost httpPost = new HttpPost(apiUrl);

        // 配置请求headers
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }
        }

        // 配置请求参数
        if (params != null && params.length() > 0) {
            //参数格式不匹配会报400 BadRequest
            StringEntity stringEntity = new StringEntity(params, ContentType.APPLICATION_JSON);
            httpPost.setEntity(stringEntity);
        }

        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            if (response == null || response.getStatusLine() == null) {
                return null;
            }

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity entityRes = response.getEntity();
                if (entityRes != null) {
                    String res = EntityUtils.toString(entityRes, HttpUtil.UTF_8);
                    logger.info("url="+apiUrl+"param"+params+"-----"+res);
                    return res;
                }
            }
            return null;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    public String doDelete(String url, Map<String, String> headers, Map<String, Object> params) {

        String headerStr = null;
        if (headers != null) {
            headerStr = JsonUtil.convertObject2Json(headers);
        }
        logger.info("doPost url:" + url + ". headers :" + headerStr + ". params :" +JsonUtil.convertObject2Json(params));

        HttpDelete httpDelete = new HttpDelete(url);

        // *) 设置header信息
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpDelete.addHeader(entry.getKey(), entry.getValue());
            }
        }

        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpDelete);
            if (response == null || response.getStatusLine() == null) {
                return null;
            }

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity entityRes = response.getEntity();
                if (entityRes != null) {
                    String res = EntityUtils.toString(entityRes, HttpUtil.UTF_8);
                    logger.info(res);
                    return res;
                }
            }
            return null;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    private StringEntity getUrlEncodedFormEntity(Map<String, Object> params) {
//        List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());
//        for (Map.Entry<String, Object> entry : params.entrySet()) {
//            NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
//            pairList.add(pair);
//        }
        return new StringEntity(JsonUtil.convertObject2Json(params), ContentType.APPLICATION_JSON);
    }

    public void shutdown() {
        idleThread.shutdown();
    }

    // 监控有异常的链接
    private class IdleConnectionMonitorThread extends Thread {

        private final HttpClientConnectionManager connMgr;
        private volatile boolean exitFlag = false;

        public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
            this.connMgr = connMgr;
            setDaemon(true);
        }

        @Override
        public void run() {
            while (!this.exitFlag) {
                synchronized (this) {
                    try {
                        this.wait(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // 关闭失效的连接
                connMgr.closeExpiredConnections();
                // 可选的, 关闭30秒内不活动的连接
                connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
            }
        }

        public void shutdown() {
            this.exitFlag = true;
            synchronized (this) {
                notify();
            }
        }

    }

}