package com.example.postreply.security;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private JwtUtil jwtUtil;

    @Autowired
    public void setJwtUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestPath= request.getRequestURI();
        if (requestPath.equals("/auth/login") || requestPath.equals("/api/users/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        if(authHeader==null || !authHeader.startsWith("Bearer ")){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid JWT token");
            return;
        }

        try{

            String token = authHeader.substring(7);
            /* will throw exception if not valid */
            jwtUtil.validateToken(token);

            Claims claims = jwtUtil.extractClaims(token);

            /* extract information from the token */
            String username = claims.getSubject();
            Long userId = claims.get("userId", Long.class);
            String email = claims.get("email", String.class);
            String role = claims.get("role", String.class);

            UserPrinciple principal = UserPrinciple.builder()
                    .userId(userId)
                    .username(username)
                    .email(email)
                    .role(role)
                    .build();

            /* make Authentication object */
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());
            /* set Authentication object to SC*/
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }catch (Exception e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid JWT token");
            return;
        }
        // Continue the filter chain if valid
        filterChain.doFilter(request, response);
    }
}


//@Override
//protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request,
//                                jakarta.servlet.http.HttpServletResponse response,
//                                jakarta.servlet.FilterChain filterChain)
//        throws jakarta.servlet.ServletException, IOException {
//
//    String authHeader = request.getHeader("Authorization");
//    if (authHeader != null && authHeader.startsWith("Bearer ")) {
//        String token = authHeader.substring(7);
//
//        try {
//            Long userId = jwtUtil.extractUserId(token);
//            String role = jwtUtil.extractRole(token);
//
//            CustomAuthenticationToken authentication = new CustomAuthenticationToken(userId, role);
//            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
//            return;
//        }
//    }
//
//    filterChain.doFilter(request, response);
//}