package com.hnttg.cibcredit.api.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    private final  static Logger logger= LoggerFactory.getLogger(WeatherService.class);

    @Autowired
    private RestTemplate restTemplate;


    public void getWeather() {
        try {

            ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://www.weather.com.cn/weather/101250101.shtml",String.class);
            if (responseEntity!=null && responseEntity.getStatusCodeValue()==200){
                ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                String source = responseEntity.getBody();
                logger.error("获取第三方天气API接口内容：{} ",source);
            } else {
                logger.error("获取第三方天气API接口异常 ");
            }
        } catch (Exception e) {
            logger.error("获取第三方天气API接口getWeather error {} ", e);
        }
    }
}
