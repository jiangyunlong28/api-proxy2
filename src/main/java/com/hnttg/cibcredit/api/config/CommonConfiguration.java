package com.hnttg.cibcredit.api.config;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.web.filter.CharacterEncodingFilter;


@Configuration
public class CommonConfiguration {

    @Bean
    public FilterRegistrationBean webFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        registration.setFilter(characterEncodingFilter);
        return registration;
    }

    @Bean
    public HttpMessageConverters messageConverters() {
        FastJsonHttpMessageConverter fastjson = new FastJsonHttpMessageConverter();
        FormHttpMessageConverter httpMessageConverter = new FormHttpMessageConverter();
        return new HttpMessageConverters(fastjson, httpMessageConverter);
    }

}
