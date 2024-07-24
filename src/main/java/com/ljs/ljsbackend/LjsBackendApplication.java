package com.ljs.ljsbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class LjsBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LjsBackendApplication.class, args);
    }

//front proxy 설정으로 대체
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedMethods("*")
//                        .allowedOrigins("http://58.148.100.28","http://localhost:5000","http://127.0.0.1:5000","http://172.30.1.19:5000","http://192.168.219.111:5000")
//                        .allowCredentials(false)
//                        .maxAge(3000);
//                //registry.addMapping("/**").allowedOriginPatterns("*");
//
//            }
//        };
//    }

}
