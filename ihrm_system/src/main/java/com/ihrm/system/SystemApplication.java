package com.ihrm.system;

import com.ihrm.common.utils.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;

import javax.persistence.Entity;

//1.配置spring boot包扫描
@SpringBootApplication(scanBasePackages = "com.ihrm")
//2.配置jap注解的扫描
@EntityScan(value = "com.ihrm.domain")
public class SystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class,args);
    }
    @Bean
    public IdWorker idWorker(){return new IdWorker();}
}
