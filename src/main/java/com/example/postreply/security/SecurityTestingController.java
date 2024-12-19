package com.example.postreply.security;


import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


/*
use this token for now (since we aren't going to startup config server, user profile, authentication server to fetch token)
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZSIsInVzZXJJZCI6MywiZW1haWwiOiJhbGljZS5qQGV4YW1wbGUuY29tIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE3MzQ1MjE5NDl9.7_5dfcY2dMQ0lSbzlnsrHNISzq-NLhdnMx9fEpxcW0o
*/
@RestController
@RequestMapping("/api")
public class SecurityTestingController {

    /*display the Authentication object within Security context*/
    @GetMapping("/secure")
    public String displayAuthObject(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        /* extract from Security Context*/
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        String info = "userId: " + userPrinciple.getUserId() + "\n" +
                "username: " + userPrinciple.getUsername() + "\n" +
                "email: " + userPrinciple.getEmail() + "\n" +
                "role: " + userPrinciple.getRole() + "\n";
        return info;
    }

    /* simply display the request from API gateway*/
    @GetMapping("/request")
    public String displayRequestInfo(HttpServletRequest request,
                                     @RequestHeader HttpHeaders headers,
                                     @RequestParam Map<String, String> queryParams
    ){
        StringBuilder info = new StringBuilder();

        // Display request path and method
        info.append("Request Method: ").append(request.getMethod()).append("\n");
        info.append("Request URI: ").append(request.getRequestURI()).append("\n");
        info.append("Query Parameters: ").append(queryParams).append("\n");

        // Display all headers
        info.append("\nHeaders:\n");
        headers.forEach((key, value) -> info.append(key).append(": ").append(value).append("\n"));


        // Display all request attributes
//        info.append("\nRequest Attributes:\n");
//        Enumeration<String> attributeNames = request.getAttributeNames();
//        while (attributeNames.hasMoreElements()) {
//            String attributeName = attributeNames.nextElement();
//            info.append(attributeName).append(": ").append(request.getAttribute(attributeName)).append("\n");
//        }

//        info.append("\nBody:\n").append(body).append("\n");

        return info.toString();
    }
}
