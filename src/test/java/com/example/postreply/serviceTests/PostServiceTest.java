package com.example.postreply.serviceTests;

import com.example.postreply.PostRepository;
import com.example.postreply.model.Post;
import com.example.postreply.service.PostService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;

/* Unit testing */
@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void shouldCreatePost(){
        // 1 - setup
        Post post = new Post();
        post.setPostId("123");
        post.setUserId(1L);
        post.setContent("Test content");

        Mockito.when(postRepository.save(post)).thenReturn(post);

        // 2 - act
        Post createdPost = postService.createPost(post);

        // assertions/testings
        Assertions.assertNotNull(createdPost);
        Assertions.assertEquals(1L, createdPost.getUserId());
        Assertions.assertEquals("123", createdPost.getPostId());
        // ensure the postRepository.save(..) only called once
        Mockito.verify(postRepository, Mockito.times(1)).save(post);
    }

}
