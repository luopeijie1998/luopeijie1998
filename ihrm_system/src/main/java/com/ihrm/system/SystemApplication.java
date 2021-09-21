package com.ihrm.system;

import com.ihrm.common.utils.IdWorker;
import com.ihrm.common.utils.JwtUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import javax.persistence.Entity;
/**
 * @author LPJ
 */
//1.配置spring boot包扫描
@SpringBootApplication(scanBasePackages = "com.ihrm")
//2.配置jap注解的扫描
@EntityScan(value = "com.ihrm.domain.system")
@EnableEurekaClient
@EnableDiscoveryClient
@EnableFeignClients
public class SystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class,args);
    }


    @Bean
    public JwtUtil jwtUtil(){return new JwtUtil();}
}
