package com.hnttg.cibcredit.api.controller.common;

import com.alibaba.fastjson.JSONObject;
import com.hnttg.cibcredit.api.model.CmpayCommonResp;
import com.hnttg.cibcredit.preposition.service.entity.common.WebConstants;
import com.hnttg.cibcredit.preposition.service.entity.response.RespEntity;
import com.hq.scrati.common.exception.BusinessException;
import com.hq.scrati.common.exception.CommonErrCode;
import com.hq.scrati.common.exception.CommonException;
import com.hq.scrati.common.exception.HqBaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by young on 6/11/15.
 */
//@EnableWebMvc
@ControllerAdvice
public class ExceptionHandleController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(ExceptionHandleController.class);

    @ExceptionHandler(value = NoHandlerFoundException.class)
    @ResponseBody
    public ResponseEntity<RespEntity> handleNoHandlerFoundException(NoHandlerFoundException ex) throws  Exception{
        RespEntity entity = new RespEntity(CommonErrCode.ARGS_INVALID.getCode()
                , "找不到处理器[" + ex.getHttpMethod().toUpperCase() + ": " + ex.getRequestURL() + "]");
        String responseTm= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        CmpayCommonResp cmpayCommonResp= new CmpayCommonResp(responseTm,entity.getKey(), entity.getMsg());
        String  respJson= JSONObject.toJSONString(cmpayCommonResp);
        return this.packResp(respJson);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ResponseEntity<RespEntity> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) throws  Exception{
        StringBuffer supportedMethods = new StringBuffer();
        if (ex.getSupportedMethods() != null && ex.getSupportedMethods().length > 0) {
            for (int i = 0; i < ex.getSupportedMethods().length; i++) {
                if (i != 0) {
                    supportedMethods.append(",");
                }
                supportedMethods.append(ex.getSupportedMethods()[i]);
            }
        }
        RespEntity entity = new RespEntity(CommonErrCode.ARGS_INVALID.getCode(),
                "不支持的Http " + ex.getMethod().toUpperCase() + " 方法, 请尝试使用[" + supportedMethods.toString() + "]");
        String responseTm= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        CmpayCommonResp cmpayCommonResp= new CmpayCommonResp(responseTm,entity.getKey(), entity.getMsg());
        String  respJson= JSONObject.toJSONString(cmpayCommonResp);
        return this.packResp(respJson);

    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseBody
    public ResponseEntity<RespEntity> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) throws  Exception{
        RespEntity entity = new RespEntity(CommonErrCode.ARGS_INVALID.getCode(),
                "不支持的Media Type[" + ex.getContentType().toString() + "]");
        String responseTm= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        CmpayCommonResp cmpayCommonResp= new CmpayCommonResp(responseTm,entity.getKey(), entity.getMsg());
        String  respJson= JSONObject.toJSONString(cmpayCommonResp);
        return this.packResp(respJson);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    @ResponseBody
    public ResponseEntity<RespEntity> handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException ex) throws  Exception{
        StringBuffer supportsList = new StringBuffer();
        if (ex.getSupportedMediaTypes() != null && ex.getSupportedMediaTypes().size() > 0) {
            for (int i = 0; i < ex.getSupportedMediaTypes().size(); i++) {
                if (i != 0) supportsList.append(",");
                supportsList.append(ex.getSupportedMediaTypes().get(i).toString());
            }
        }
        RespEntity entity = new RespEntity(CommonErrCode.ARGS_INVALID.getCode(),
                "不支持的Media Type, 请尝试使用[" + supportsList.toString() + "]");
        String responseTm= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        CmpayCommonResp cmpayCommonResp= new CmpayCommonResp(responseTm,entity.getKey(), entity.getMsg());
        String  respJson= JSONObject.toJSONString(cmpayCommonResp);
        return this.packResp(respJson);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public ResponseEntity<RespEntity> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex) throws  Exception{
        RespEntity entity = new RespEntity(
                CommonErrCode.ARGS_INVALID.getCode(),
                "URL参数不能为空[" + ex.getParameterName() + "]");
        String responseTm= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        CmpayCommonResp cmpayCommonResp= new CmpayCommonResp(responseTm,entity.getKey(), entity.getMsg());
        String  respJson= JSONObject.toJSONString(cmpayCommonResp);
        return this.packResp(respJson);
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    @ResponseBody
    public ResponseEntity<RespEntity> handleServletRequestBindingException(ServletRequestBindingException ex) throws  Exception{
        RespEntity entity = new RespEntity(
                CommonErrCode.ARGS_INVALID.getCode(), "请求参数绑定错误");
        String responseTm= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        CmpayCommonResp cmpayCommonResp= new CmpayCommonResp(responseTm,entity.getKey(), entity.getMsg());
        String  respJson= JSONObject.toJSONString(cmpayCommonResp);
        return this.packResp(respJson);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<RespEntity> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) throws  Exception{
        Map<String, Object> results = new HashMap<>();
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if (fieldErrors != null && fieldErrors.size() > 0) {
            for (int i = 0; i < fieldErrors.size(); i++) {
                FieldError fieldError = fieldErrors.get(i);
                String value = ((fieldError.getRejectedValue() == null) ? "null" : fieldError.getRejectedValue().toString());
                String reason = ((fieldError.getDefaultMessage() == null) ? "" : fieldError.getDefaultMessage());
                String errorFieldMessage = "Value=" + value + "&Reason=" + reason + "";
                results.put(fieldError.getField(), errorFieldMessage);
            }
        }
        RespEntity entity = new RespEntity(
                CommonErrCode.ARGS_INVALID.getCode(), "请求参数格式错误", results);
        String responseTm= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        CmpayCommonResp cmpayCommonResp= new CmpayCommonResp(responseTm,entity.getKey(), entity.getMsg());
        String  respJson= JSONObject.toJSONString(cmpayCommonResp);
        return this.packResp(respJson);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseBody
    public ResponseEntity<RespEntity> handleMissingServletRequestPartException(MissingServletRequestPartException ex) throws  Exception{
        RespEntity entity = new RespEntity(
                CommonErrCode.ARGS_INVALID.getCode(),
                "Request Part不能为空[" + ex.getRequestPartName() + "]");
        String responseTm= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        CmpayCommonResp cmpayCommonResp= new CmpayCommonResp(responseTm,entity.getKey(), entity.getMsg());
        String  respJson= JSONObject.toJSONString(cmpayCommonResp);
        return this.packResp(respJson);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity<RespEntity> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) throws  Exception{
        logger.error("", ex);
        RespEntity entity = new RespEntity(
                CommonErrCode.ARGS_INVALID.getCode(), WebConstants.DEFAULT_ERROR_MSG);
        String responseTm= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        CmpayCommonResp cmpayCommonResp= new CmpayCommonResp(responseTm,entity.getKey(), entity.getMsg());
        String  respJson= JSONObject.toJSONString(cmpayCommonResp);
        return this.packResp(respJson);
    }

    @ExceptionHandler(ConversionNotSupportedException.class)
    @ResponseBody
    public ResponseEntity<RespEntity> handleConversionNotSupportedException(ConversionNotSupportedException ex) throws  Exception{
        RespEntity entity = new RespEntity(
                CommonErrCode.ARGS_INVALID.getCode(),
                "类型转换出错[" + ex.getPropertyName() + "]");
        String responseTm= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        CmpayCommonResp cmpayCommonResp= new CmpayCommonResp(responseTm,entity.getKey(), entity.getMsg());
        String  respJson= JSONObject.toJSONString(cmpayCommonResp);
        return this.packResp(respJson);
    }

   /* @ExceptionHandler(value = {CommonException.class, BusinessException.class})
    @ResponseBody
    public ResponseEntity<RespEntity> handleCommonException(HqBaseException ex) throws  Exception{
        HttpStatus httpStatus;
        if (CommonErrCode.ARGS_INVALID.getCode().equals(ex.getErrCode())) {
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (CommonErrCode.AUTH_TOKEN_INVALID.getCode().equals(ex.getErrCode())) {
            httpStatus = HttpStatus.UNAUTHORIZED;
        } else if (CommonErrCode.NO_PERMISSION.getCode().equals(ex.getErrCode())) {
            httpStatus = HttpStatus.FORBIDDEN;
        } else if (CommonErrCode.NO_DATA_FOUND.getCode().equals(ex.getErrCode())) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else if (CommonErrCode.BUSINESS.getCode().equals(ex.getErrCode())
                || CommonErrCode.BAD_REQUEST.getCode().equals(ex.getErrCode())) {
            httpStatus = HttpStatus.BAD_REQUEST;
        } else {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        String errorMsg = WebConstants.ERROR_MSG_KEYWORD_REGEX.matcher(ex.getErrMsg()).find()
                ? WebConstants.DEFAULT_ERROR_MSG : ex.getErrMsg();
        RespEntity entity = new RespEntity(ex.getErrCode(), errorMsg, ex.getExt());
        String responseTm= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        CmpayCommonResp cmpayCommonResp= new CmpayCommonResp(responseTm,entity.getKey(), entity.getMsg());
        String  respJson= JSONObject.toJSONString(cmpayCommonResp);
        return this.packResp(respJson);
    }*/

    @ExceptionHandler(value = {RuntimeException.class, Exception.class, Throwable.class})
    @ResponseBody
    public ResponseEntity handleException(Throwable th) throws  Exception{
        logger.error(th.getMessage(), th);
        String responseTm= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        CmpayCommonResp cmpayCommonResp= new CmpayCommonResp(responseTm,CommonErrCode.BUSINESS.getCode(), WebConstants.DEFAULT_ERROR_MSG);
        String  respJson= JSONObject.toJSONString(cmpayCommonResp);
        return this.packResp(respJson);
    }
}