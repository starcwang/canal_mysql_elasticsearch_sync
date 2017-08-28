package com.star.sync.elasticsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0
 * @since 2017-08-25 17:26:00
 */
@SpringBootApplication
@EnableScheduling
public class CanalMysqlElasticsearchSyncApplication {

    public static void main(String[] args) {
        SpringApplication.run(CanalMysqlElasticsearchSyncApplication.class, args);
    }
}
