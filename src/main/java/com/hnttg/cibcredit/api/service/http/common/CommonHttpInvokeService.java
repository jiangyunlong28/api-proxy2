package com.hnttg.cibcredit.api.service.http.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hnttg.cibcredit.api.model.CmpayCommonResp;
import com.hnttg.cibcredit.preposition.service.entity.response.RespEntity;
import com.hq.scrati.common.exception.CommonErrCode;
import com.hq.scrati.common.exception.CommonException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CommonHttpInvokeService {

    private static Logger logger = LoggerFactory.getLogger(CommonHttpInvokeService.class);
    @Autowired
    private HttpClientProvider provider;


    public String  doPostJson(String  json, String url) {
        HttpPost httpPost = null;
        String  respJson = null;
        CloseableHttpResponse response = null;
        CmpayCommonResp cmpayCommonResp=null;
        String responseTm= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        try {
            httpPost = this.provider.getHttpPost(url);
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=utf-8");
            httpPost.setEntity(new StringEntity(json,"UTF-8"));
            response = this.provider.getHttpClient().execute(httpPost);

            logger.warn("<<< BUSINESS PROCESSING FAILED status: {}", response.getStatusLine().getStatusCode());
            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                respJson = EntityUtils.toString(response.getEntity(), "UTF-8");
                logger.warn("<<< Callback Response String : {}", respJson);
            }/*else if (HttpStatus.SC_INTERNAL_SERVER_ERROR == response.getStatusLine().getStatusCode()) {
                respJson = EntityUtils.toString(response.getEntity(), "UTF-8");
                logger.warn("<<< Callback Response String : {}", respJson);
                JSONObject jsonObject=JSONObject.parseObject(respJson);
                if(jsonObject.size()>0 && jsonObject.containsKey("key") &&  jsonObject.containsKey("msg")){
                    throw  new CommonException(jsonObject.getString("key"), jsonObject.getString("msg"));
                }else {
                    throw new CommonException(CommonErrCode.UNKNOW_ERROR.getCode(), CommonErrCode.UNKNOW_ERROR.getDesc());
                }
            }*/ else {
                logger.warn("<<< BUSINESS PROCESSING FAILED URL: {}, PARAMS: {}", url, json);
                throw new CommonException(CommonErrCode.UNKNOW_ERROR.getCode(), CommonErrCode.UNKNOW_ERROR.getDesc());
            }
        }catch (CommonException ex) {
            logger.warn("<<< BUSINESS PROCESSING ERROR URL: {}, PARAMS: {}", url, json);
            logger.warn(ex.getMessage());
            throw ex;
        }  catch (Throwable th) {
            logger.warn("<<< BUSINESS PROCESSING ERROR URL: {}, PARAMS: {}", url, json);
            logger.warn(th.getMessage());
            throw new CommonException(CommonErrCode.UNKNOW_ERROR.getCode(), CommonErrCode.UNKNOW_ERROR.getDesc());
        } finally {
            this.releaseConnectionIfNecessary(httpPost, response);
        }
        return respJson;
    }

    public String  doPostFormUrlencoded(Map<String, Object> bizParams, String url) {
        HttpPost httpPost = null;
        String  respJson = null;
        CloseableHttpResponse response = null;
        try {
            httpPost = this.provider.getHttpPost(url);
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");

            List<NameValuePair> nvPairs = new ArrayList<>();
            bizParams.forEach((k, v) -> nvPairs.add(new BasicNameValuePair(k, v.toString())));
            httpPost.setEntity(new UrlEncodedFormEntity(nvPairs, "UTF-8"));
            response = this.provider.getHttpClient().execute(httpPost);

            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                respJson = EntityUtils.toString(response.getEntity(), "UTF-8");
                logger.warn("<<< Callback Response String : {}", respJson);

            } else {
                logger.warn("<<< BUSINESS PROCESSING FAILED URL: {}, PARAMS: {}", url, bizParams);
                throw new CommonException(CommonErrCode.UNKNOW_ERROR, "BUSINESS PROCESSING FAILURE");
            }
        } catch (Throwable th) {
            logger.warn("<<< BUSINESS PROCESSING ERROR URL: {}, PARAMS: {}", url, bizParams);
            throw new CommonException(CommonErrCode.UNKNOW_ERROR, "BUSINESS PROCESSING FAILURE");
        } finally {
            this.releaseConnectionIfNecessary(httpPost, response);
        }
        return respJson;
    }

    private void releaseConnectionIfNecessary(HttpPost httpPost, CloseableHttpResponse response) {
        if (httpPost != null) {
            try {
                httpPost.releaseConnection();
            } catch (Throwable th) {
                logger.error("<! Release Connection Failed", th);
            }
        }
        if (response != null) {
            try {
                response.close();
            } catch (Throwable th) {
                logger.error("<! Close Response Failed", th);
            }
        }
    }
}
