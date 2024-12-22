package com.example.postreply.serviceTests;

import com.example.postreply.AOP.Exceptions.NotFoundException;
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

import java.util.*;

import static org.mockito.Mockito.times;

/* Unit testing */
@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    /* mock repo(external) source, here we are only testing out the Service class*/
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

    @Test
    void shouldGetPostById() {
        Post post = new Post();
        post.setPostId("1");

        Mockito.when(postRepository.findById("1")).thenReturn(Optional.of(post));

        Post retrievedPost = postService.getPostById("1");

        Assertions.assertNotNull(retrievedPost);
        Assertions.assertEquals("1", retrievedPost.getPostId());
        Mockito.verify(postRepository, times(1)).findById("1");
    }

    @Test
    void shouldThrowNotFoundExceptionWhenPostNotFound() {
        Mockito.when(postRepository.findById("1")).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> postService.getPostById("1"));
        Mockito.verify(postRepository, times(1)).findById("1");
    }

    @Test
    void shouldGetPublishedPosts() {
        // 1 - setup
        List<Post> posts = Arrays.asList(new Post(), new Post());

        Mockito.when(postRepository.findByStatus("Published")).thenReturn(posts);

        // 2 - act
        List<Post> publishedPosts = postService.getPublishedPosts();

        //3 - assertions/testing
        Assertions.assertNotNull(publishedPosts);
        Assertions.assertEquals(2, publishedPosts.size());
        Mockito.verify(postRepository, times(1)).findByStatus("Published");
    }

    @Test
    void shouldUpdatePostStatus() {
        Post post = new Post();
        post.setPostId("1");
        post.setStatus("Draft");

        Mockito.when(postRepository.findById("1")).thenReturn(Optional.of(post));
        Mockito.when(postRepository.save(post)).thenReturn(post);

        Post updatedPost = postService.updatePostStatus("1", "Published");

        Assertions.assertNotNull(updatedPost);
        Assertions.assertEquals("Published", updatedPost.getStatus());
        // check if .findById only called once, and if .save only called once
        Mockito.verify(postRepository, times(1)).findById("1");
        Mockito.verify(postRepository, times(1)).save(post);
    }

    @Test
    void shouldDeletePost() {
        Post post = new Post();
        post.setPostId("1");

        Mockito.when(postRepository.findById("1")).thenReturn(Optional.of(post));

        postService.deletePost("1", 1L, true);

        Mockito.verify(postRepository, times(1)).findById("1");
        Mockito.verify(postRepository, times(1)).save(post);
        Assertions.assertEquals("Deleted", post.getStatus());
    }

    @Test
    void shouldAddReplyToPost() {
        Post post = new Post();
        post.setPostId("1");
        post.setPostReplies(new ArrayList<>());

        Post.PostReply reply = new Post.PostReply();
        reply.setComment("Test Reply");

        Mockito.when(postRepository.findById("1")).thenReturn(Optional.of(post));

        postService.addReplyToPost("1", reply);

        Mockito.verify(postRepository, times(1)).findById("1");
        Mockito.verify(postRepository, times(1)).save(post);
        Assertions.assertEquals(1, post.getPostReplies().size());
        Assertions.assertEquals("Test Reply", post.getPostReplies().get(0).getComment());
    }

    @Test
    void shouldAddSubReplyToReply() {
        Post post = new Post();
        post.setPostId("1");
        Post.PostReply reply = new Post.PostReply();
        reply.setSubReplies(new ArrayList<>());
        post.setPostReplies(Collections.singletonList(reply));

        Post.PostReply.SubReply subReply = new Post.PostReply.SubReply();
        subReply.setComment("Test SubReply");

        Mockito.when(postRepository.findById("1")).thenReturn(Optional.of(post));

        postService.addSubReplyToReply("1", 0, subReply);

        Mockito.verify(postRepository, times(1)).findById("1");
        Mockito.verify(postRepository, times(1)).save(post);
        Assertions.assertEquals(1, reply.getSubReplies().size());
        Assertions.assertEquals("Test SubReply", reply.getSubReplies().get(0).getComment());
    }

    @Test
    void shouldDeleteReply() {
        Post post = new Post();
        post.setPostId("1");
        Post.PostReply reply = new Post.PostReply();
        reply.setIsActive(true);
        post.setPostReplies(Collections.singletonList(reply));

        Mockito.when(postRepository.findById("1")).thenReturn(Optional.of(post));

        postService.deleteReply("1", 0, 1L, true);

        Mockito.verify(postRepository, times(1)).findById("1");
        Mockito.verify(postRepository, times(1)).save(post);
        Assertions.assertFalse(reply.getIsActive());
    }

}
