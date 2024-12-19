//package com.example.postreply;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//
//@Configuration
//public class CorsConfig {
//
//    @Bean
//    public CorsFilter corsFilter() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.addAllowedOriginPattern("*"); // 允许所有来源，也可以指定具体域名，如 "http://localhost:5173"
//        config.addAllowedMethod("*");        // 允许所有HTTP方法：GET, POST, PUT, DELETE等
//        config.addAllowedHeader("*");        // 允许所有头部
//        config.setAllowCredentials(true);    // 允许携带cookie等凭据
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config); // 配置所有路径
//        return new CorsFilter(source);
//    }
//}
