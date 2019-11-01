package com.hnttg.cibcredit.api.controller.common;

import com.hnttg.cibcredit.api.util.WebUtils;
import org.joda.time.DateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
public class PingController extends BaseController {

    public PingController() {
    }

    @RequestMapping({"/ping"})
    public ResponseEntity<Map<String, Object>> ping(HttpServletRequest request) {
        Map<String, Object> results = new HashMap();
        results.put("Server-Name", "Api-Web");
        results.put("Server-Time", DateTime.now().toString("YYYY-MM-dd HH:mm:ss"));
        results.put("Client-Ip", WebUtils.getRequestIpAddress(request));
        results.put("UserAgent", request.getHeader("User-Agent"));
        results.put("ContentType", request.getHeader("Content-Type"));
        results.put("Method", request.getMethod());
        results.put("AID", request.getHeader("AID"));
        results.put("Message", "I am alive!");
        return this.getJsonResp(results, HttpStatus.OK);
    }
}
