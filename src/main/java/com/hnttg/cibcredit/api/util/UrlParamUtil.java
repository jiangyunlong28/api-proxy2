package com.hnttg.cibcredit.api.util;

import org.springframework.util.StringUtils;

import java.net.URLEncoder;
import java.util.*;


public class UrlParamUtil {

    public static Map<String, String> fromLinkString(String linkString) {
        Map<String, String> params = new HashMap<>();
        String[] items = linkString.split("&");
        for (String item : items) {
            if (!StringUtils.isEmpty(item) && item.contains("=")) {
                String[] kv = item.split("=");
                if (kv.length != 2 || StringUtils.isEmpty(kv[0]) || StringUtils.isEmpty(kv[1])) {
                    continue;
                }
                params.put(kv[0], kv[1]);
            }
        }
        return params;
    }

    public static String createLinkString(Map<String, Object> params) {
        return createLinkString(params, false);
    }

    public static String createLinkString(Map<String, Object> params, Boolean encodeValue) {
        if (params == null || params.size() == 0)
            return "";
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        StringBuffer linkString = new StringBuffer();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            Object value = params.get(key);
            if (value == null)
                continue;
            if (encodeValue) {
                try {
                    value = URLEncoder.encode(value.toString(), "UTF-8");
                } catch (Throwable th) {
                    continue;
                }
            }
            if (linkString.length() > 0)
                linkString.append("&");
            linkString.append(key).append("=").append(value);
        }
        return linkString.toString();
    }
}
