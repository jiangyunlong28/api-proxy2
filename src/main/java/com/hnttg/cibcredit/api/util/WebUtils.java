package com.hnttg.cibcredit.api.util;


import com.hq.scrati.common.exception.CommonErrCode;
import com.hq.scrati.common.exception.CommonException;
import com.hq.scrati.common.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * @author Yan
 * @create 2017/9/12
 */
public class WebUtils {

    public static final Pattern REX_PATH_VARIABLE_PATTERN = Pattern.compile("\\{\\w*(.+(jpg|jpeg|gif|png|xls|xlsx|doc|docx|txt|cvs|html|pdf|zip))?\\}");
    public static final String REX_PATH_VARIABLE_REPLACE = "\\\\w*(.+(jpg|jpeg|gif|png|xls|xlsx|doc|docx|txt|cvs|html|pdf|zip))?";

    private static BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    public static String getAuthToken(HttpServletRequest request) {
        return WebUtils.getParam(request, "TOKEN");
    }

    private static void checkSubscriptionInvokeUri(HttpServletRequest request) {
        String requestURI = StringUtils.removeEnd(request.getRequestURI(), "/");
        if (!requestURI.startsWith("/user")
                && !requestURI.startsWith("/sub")) {
            throw new CommonException(CommonErrCode.NO_PERMISSION, "无权访问该服务[" + requestURI + "]");
        }
    }

    private static String getParam(HttpServletRequest request, String name) {
        String value = request.getHeader(name);
        return StringUtils.isNotBlank(value) ? value : WebUtils.getParam(request.getCookies(), name);
    }

    public static String getParam(Cookie[] cookies, String name) {
        if (cookies == null || cookies.length == 0) return "";
        Cookie cookie = Arrays.stream(cookies).filter(
                c -> name.equals(c.getName())).findFirst().orElse(null);
        return cookie == null ? null : cookie.getValue();
    }

    public static Cookie createCookie(String name, String value, Integer maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }

    private static Boolean usingCookie(String systemId) {
        return true;
    }


    public static String getRequestIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (org.springframework.util.StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (org.springframework.util.StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (org.springframework.util.StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (org.springframework.util.StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (org.springframework.util.StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("X-Real-IP");
        }
        if (org.springframework.util.StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        String[] ipAddresses = ipAddress.split(",");
        if (ipAddresses.length > 1) {
            ipAddress = ipAddresses[0];
        }
        return "0:0:0:0:0:0:0:1".equals(ipAddress) ? "127.0.0.1" : ipAddress;
    }

    private static String[] IE_BROWSER_SIGNALS = {"msie", "trident", "edge", "rv:11.0"};

    public static boolean isMSBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent").toLowerCase();
        for (String signal : IE_BROWSER_SIGNALS) {
            if (userAgent.contains(signal))
                return true;
        }
        return false;
    }

    public static BigDecimal convertToYuan(String data) {
        return new BigDecimal(data).divide(ONE_HUNDRED).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal convertToYuan(Long data) {
        return new BigDecimal(data).divide(ONE_HUNDRED).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
