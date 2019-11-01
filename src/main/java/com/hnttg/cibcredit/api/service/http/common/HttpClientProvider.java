package com.hnttg.cibcredit.api.service.http.common;

import com.hq.scrati.common.util.UrlParamUtil;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.Closeable;
import java.util.Map;

@Service
public class HttpClientProvider {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientProvider.class);

    private CloseableHttpClient httpClient;
    private PoolingHttpClientConnectionManager connManager;

    public HttpClientProvider() {
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            // 全部信任 不做身份鉴定
            builder.loadTrustMaterial(null, (x509Certificates, s) -> true);
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    builder.build(), new String[] { "SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2" },
                    null, NoopHostnameVerifier.INSTANCE);
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", new PlainConnectionSocketFactory())
                    .register("https", sslsf)
                    .build();
            this.connManager = new PoolingHttpClientConnectionManager(registry);
            this.connManager.setMaxTotal(500);
            this.connManager.setDefaultMaxPerRoute(500);
            this.httpClient = HttpClients.custom()
                    .setConnectionManager(this.connManager).build();
            new IdleConnectionCleanThread(this.connManager);
        } catch (Throwable th) {
            logger.error("<<<<<< Init Http Executor On Error", th);
        }
    }

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    public HttpGet getHttpGet(String url) {
        return getHttpGet(url, null, false);
    }

    public HttpGet getHttpGet(String url, Map<String, Object> params) {
        return getHttpGet(url, params, false);
    }

    public HttpGet getHttpGet(String url, Map<String, Object> params, Boolean uriEncode) {
        String queryStr = UrlParamUtil.createLinkString(params, uriEncode);
        if (!StringUtils.isEmpty(queryStr)) queryStr = "?" + queryStr;
        HttpGet httpGet = new HttpGet(url + queryStr);
        httpGet.setConfig(RequestConfig.custom()
                .setConnectionRequestTimeout(5000)
                .setConnectTimeout(15000)
                .setSocketTimeout(45000).build()
        );
        return httpGet;
    }

    public HttpPost getHttpPost(String url) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(RequestConfig.custom()
                .setConnectionRequestTimeout(5000)
                .setConnectTimeout(15000)
                .setSocketTimeout(45000).build()
        );
        return httpPost;
    }

    public static void close(HttpRequestBase request, Closeable response) {
        if(request != null) {
            try { request.releaseConnection(); } catch (Throwable th) { }
        }
        if(response != null) {
            try { response.close(); } catch (Throwable th) { }
        }
    }

}
