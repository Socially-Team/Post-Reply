package com.example.postreply.controllerTests;

import com.example.postreply.controller.PostController;
import com.example.postreply.model.Post;
import com.example.postreply.security.JwtUtil;
import com.example.postreply.security.UserPrinciple;
import com.example.postreply.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.security.Security;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtUtil jwtUtil; // Mock the JwtUtil dependency


    @BeforeEach
    void setup(){
        // Mock SecurityContext and Authentication
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        UserPrinciple userPrinciple = UserPrinciple.builder()
                .userId(1L)
                .username("testuser")
                .role("USER")
                .build();

        Mockito.when(authentication.getPrincipal()).thenReturn(userPrinciple);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void shouldCreatePost() throws Exception{
        Post post = new Post();
        post.setContent("Test post content");

        Post createdPost = new Post();
        createdPost.setPostId("1");
        createdPost.setUserId(1L);
        createdPost.setContent("Test post content");
        createdPost.setStatus("Unpublished");

        Mockito.when(postService.createPost(Mockito.any(Post.class))).thenReturn(createdPost);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(post))
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.userId").value("1"))
                .andExpect(jsonPath("$.status").value("Unpublished"));
    }

}
