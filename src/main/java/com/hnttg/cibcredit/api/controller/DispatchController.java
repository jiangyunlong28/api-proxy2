package com.hnttg.cibcredit.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.hnttg.cibcredit.api.controller.common.BaseController;
import com.hnttg.cibcredit.api.model.CmpayCommonResp;
import com.hnttg.cibcredit.api.service.http.common.CommonHttpInvokeService;
import com.hnttg.cibcredit.preposition.service.entity.common.WebConstants;
import com.hnttg.cibcredit.preposition.service.entity.request.zj.ZJBaseReq;
import com.hnttg.cibcredit.preposition.service.entity.response.RespEntity;
import com.hq.scrati.common.exception.CommonErrCode;
import com.hq.scrati.common.exception.CommonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class DispatchController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(DispatchController.class);

    @Value("${cmpay.forward.url}")
    private  String forwardUrl;

    @Autowired
    CommonHttpInvokeService httpInvokeService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<RespEntity> heartbeatCheck() {
        return getMessageResp("SUCCESS");
    }


    @RequestMapping(value = "/cmpay/forward/**", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity forward(@RequestBody ZJBaseReq req, HttpServletRequest request) throws Exception{
        String  respJson = null;
        String url="";
        String responseTm=LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        CmpayCommonResp cmpayCommonResp =null;
        try {
            url=forwardUrl+request.getRequestURI().replaceAll("/cmpay/forward/","");
            respJson = httpInvokeService.doPostJson(JSONObject.toJSONString(req), url);
           // respJson = httpInvokeService.doPostFormUrlencoded(getParamsMap(request), url);
        }catch (CommonException e){
            logger.error(e.getMessage(), e);
            cmpayCommonResp= new CmpayCommonResp(responseTm,e.getErrCode(), e.getErrMsg());
            respJson=JSONObject.toJSONString(cmpayCommonResp);
        }catch (Exception e) {
            logger.error(e.getMessage(), e);
            cmpayCommonResp= new CmpayCommonResp(responseTm,CommonErrCode.BUSINESS.getCode(), WebConstants.DEFAULT_ERROR_MSG);
            respJson=JSONObject.toJSONString(cmpayCommonResp);
        }
        logger.info("respJson:"+respJson);
        return  this.getJsonResp(respJson,HttpStatus.OK);
       // return this.packResp(respJson);
    }
}
