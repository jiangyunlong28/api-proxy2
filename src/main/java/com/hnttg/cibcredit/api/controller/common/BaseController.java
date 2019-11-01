package com.hnttg.cibcredit.api.controller.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hnttg.cibcredit.api.util.XXTEA;
import com.hnttg.cibcredit.preposition.service.entity.request.zj.ZJBaseReq;
import com.hnttg.cibcredit.preposition.service.entity.response.RespEntity;
import com.hq.scrati.common.exception.CommonErrCode;
import com.hq.scrati.common.exception.CommonException;
import com.hq.scrati.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by young on 6/11/15.
 */
@RestController
public class BaseController {

    public final static String DEFAULT_LIST_PAGE_INDEX = "0";
    public final static String DEFAULT_LIST_PAGE_SIZE = "20";
    @Value("${zj.xxtea.key}")
    private String xxteaKey;
    @Value("${zj.clientId}")
    private String clientId;
    @Value("${zj.clientUserId}")
    private String clientUserId;

    public <T> ResponseEntity<T> getJsonResp(T body, HttpStatus statusCode) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=utf-8");
        return new ResponseEntity<>(body, responseHeaders, statusCode);
    }

    public ResponseEntity<RespEntity> getMessageResp(String message) {
        RespEntity respEntity = new RespEntity();
        respEntity.setKey(CommonErrCode.NONE.getCode());
        respEntity.setMsg(message);
        return getJsonResp(respEntity, HttpStatus.OK);
    }


    public String getAppId(HttpServletRequest request) {
        return request.getHeader("AID");
    }

    public String getSystemId(HttpServletRequest request) {
        return request.getHeader("SID");
    }

    public Integer translatePage(Integer startRecord, Integer maxRecords) {
        return (startRecord / maxRecords) + 1;//page从1开始
    }

    public Map<String, Object> translateStandardBizParams(HttpServletRequest request) {
        Map<String, Object> bizParams = new HashMap<>();
        Map<String, String> req = new HashMap<>();
        Enumeration<?> queryNames = request.getParameterNames();
        while (queryNames.hasMoreElements()) {
            String name = (String) queryNames.nextElement();
            if (name.equals("page") || name.equals("pageSize")) {
                bizParams.put(name, request.getParameter(name));
            } else {
                req.put(name, request.getParameter(name));
            }
        }
        bizParams.put("req", req);
        this.setDefaultParamsIfNecessary(bizParams);
        return bizParams;
    }

    public Map<String, Object> getParamsMap(HttpServletRequest request) {
        Map<String, Object> params = new HashMap<>();
        Enumeration<?> queryNames = request.getParameterNames();
        while (queryNames.hasMoreElements()) {
            String name = (String) queryNames.nextElement();
            params.put(name, request.getParameter(name));
        }
        return params;
    }

    public Map<String, String> getParamsMap2(HttpServletRequest request) {
        Map<String, String> _param = new HashMap<>();
        if (request.getParameterMap() != null && request.getParameterMap().size() > 0) {
            for (Object paramKey : request.getParameterMap().keySet()) {
                if (paramKey != null) {
                    String paramKeyStr = paramKey.toString();
                    _param.put(paramKeyStr, request.getParameter(paramKeyStr));
                }
            }
        }
        return  _param;
    }

    public Map<String, Object> translateSimpleBizParams(HttpServletRequest request) {
        Map<String, Object> bizParams = new HashMap<>();
        Enumeration<?> queryNames = request.getParameterNames();
        while (queryNames.hasMoreElements()) {
            String name = (String) queryNames.nextElement();
            bizParams.put(name, request.getParameter(name));
        }
        this.setDefaultParamsIfNecessary(bizParams);
        return bizParams;
    }

    private void setDefaultParamsIfNecessary(Map<String, Object> bizParams) {
        if (!bizParams.containsKey("page"))
            bizParams.put("page", Integer.valueOf(DEFAULT_LIST_PAGE_INDEX));
        if (!bizParams.containsKey("pageSize"))
            bizParams.put("pageSize", Integer.valueOf(DEFAULT_LIST_PAGE_SIZE));
    }
    public <T> T unpackReq(ZJBaseReq req, Class<T> clazz) throws IOException {
        String reqJson = new String(XXTEA.decryptWithBase64(req.getReqData().getBytes(), xxteaKey.getBytes()));
        reqJson = reqJson.substring(0,reqJson.length() - 20);
        String reqSign = reqJson.substring(reqJson.length() - 20);
        if(!reqSign.equals(sign(reqJson))){
            throw new CommonException(CommonErrCode.ARGS_INVALID, "签名错误");
        }
        return JSON.parseObject(reqJson, clazz);
    }
    public String sign(String jsonStr) throws UnsupportedEncodingException {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        String codes = "";
        try {
            paramMap = getJson(jsonStr);
            codes = requestDataSort(paramMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        codes = URLEncoder.encode(codes, "UTF-8");
        String messageSignStr = SHA_1(codes);
        return messageSignStr;
    }
    public ResponseEntity packResp(String jsonStr) throws UnsupportedEncodingException {
        String messageSignStr = sign(jsonStr);

        messageSignStr = jsonStr + messageSignStr;

        String jsonValue = XXTEA.encryptWithBase64(messageSignStr.getBytes(), xxteaKey.getBytes());
        JSONObject resp = new JSONObject();
        resp.put("rspData", jsonValue);

        return getJsonResp(resp, HttpStatus.OK);
    }

    public String SHA_1(String decript) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public Map<String, Object> getJson(String jsonStr) throws Exception {
        Map<String, Object> map = new HashMap<>();
        if (!StringUtils.isEmpty(jsonStr))
        {
            JSONObject json = JSON.parseObject(jsonStr);
            for (String key : json.keySet()) {
                if (key.equals("baseInfo"))
                {
                    JSONObject val = json.getJSONObject(key);
                    for (String key1 : val.keySet()) {
                        String value = val.getString(key1);
                        if (value.indexOf("{") == 0) {
                            map.put(key1.trim(), getJson(value));
                        } else if (value.indexOf("[") == 0) {
                            map.put(key1.trim(), getList(value));
                        } else {
                            map.put(key1.trim(), value.trim());
                        }
                    }
                }
                else
                {
                    String value = json.getString(key);
                    if (value.indexOf("{") == 0) {
                        map.put(key.trim(), getJson(value));
                    } else if (value.indexOf("[") == 0) {
                        map.put(key.trim(), getList(value));
                    } else {
                        map.put(key.trim(), value.trim());
                    }
                }
            }
        }
        return map;
    }

    /**
     * 将单个json数组字符串解析放在list中
     *
     * @param jsonStr
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getList(String jsonStr) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        JSONArray ja = JSON.parseArray(jsonStr);
        for (int j = 0; j < ja.size(); j++) {
            String jm = ja.get(j) + "";
            if (jm.indexOf("{") == 0) {
                Map<String, Object> m = getJson(jm);
                list.add(m);
            }
        }
        return list;
    }

    private String requestDataSort(Map<String, Object> map) throws Exception {
        String[] keyArray = (String[]) map.keySet().toArray(new String[0]);
        Arrays.sort(keyArray);

        StringBuilder stringBuilder = new StringBuilder();
        String[] arrayOfString1;
        int j = (arrayOfString1 = keyArray).length;
        for (int i = 0; i < j; i++) {
            String key = arrayOfString1[i];
            String mapValue = map.get(key).toString();
            if (mapValue.startsWith("[{") && mapValue.endsWith("}]")) {
                mapValue = mapValue.substring(1, mapValue.length());
                mapValue = mapValue.substring(0, mapValue.length() - 1);
                if (StringUtils.isBlank(mapValue)) {
                    stringBuilder.append(key).append("{}");
                }else {
                    Map<String, Object> tempMap = new HashMap<String, Object>();
                    tempMap = convertStringToMap(mapValue);
                    String requestData = requestDataSort(tempMap);
                    stringBuilder.append(key).append("{").append(requestData).append("}");
                }
            } else {
                stringBuilder.append(key).append(mapValue);
            }
        }
        return stringBuilder.toString();
    }
    public Map<String, Object> convertStringToMap(String request) {
        if (request.startsWith("{")) {
            request = request.substring(1, request.length());
        }
        if (request.endsWith("}")) {
            request = request.substring(0, request.length() - 1);
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        String[] out = request.split(",");
        for (String anOut : out) {
            String key = anOut.substring(0, anOut.indexOf("="));
            String value = anOut.substring(anOut.indexOf("=")+1, anOut.length());
            paramMap.put(key.trim(), value.trim());
        }
        return paramMap;
    }
}
