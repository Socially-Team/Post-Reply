package com.example.postreply;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


public class CustomAuthenticationToken extends AbstractAuthenticationToken {

    private final Long principal; // 用户ID（Long）
    private final String role;

    public CustomAuthenticationToken(Long userId, String role) {
        super(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));
        this.principal = userId;
        this.role = role;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Long getPrincipal() {
        return this.principal;
    }

    public String getRole() {
        return this.role;
    }
}