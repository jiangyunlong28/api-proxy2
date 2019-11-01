package com.hnttg.cibcredit.api.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 *
 */
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        MongoDataAutoConfiguration.class,
        MongoAutoConfiguration.class
})
@ComponentScan({"com.hnttg","com.hq"})
public class ApiWebStarter {
    public static void main(String[] args) {
        SpringApplication.run(ApiWebStarter.class, args);
    }
}

