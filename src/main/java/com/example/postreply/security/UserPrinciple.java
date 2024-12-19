package com.example.postreply.security;

/*
only consists of:
username, userId, email, role
this is only for creating an Authentication object for setting Security Context (after validate the token)
*/

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserPrinciple {
    private String username;
    private Long userId;
    private String email;
    private String role;
}
