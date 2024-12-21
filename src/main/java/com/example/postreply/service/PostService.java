package com.example.postreply.service;


import com.example.postreply.AOP.Exceptions.NotFoundException;
import com.example.postreply.PostRepository;
import com.example.postreply.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    /**
     * 创建新帖子，初始状态在 Controller 中指定（例如 Unpublished）
     */
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    /**
     * 保存（更新）帖子
     */
    public Post savePost(Post post) {
        return postRepository.save(post);
    }

    /**
     * 根据 postId 获取帖子，如未找到则抛出异常
     */
    public Post getPostById(String postId) {
        return postRepository.findById(postId).orElseThrow(() -> new NotFoundException("Post not found"));
    }

    /**
     * 获取所有已发布的帖子
     */
    public List<Post> getPublishedPosts() {
        return postRepository.findByStatus("Published");
    }

    /**
     * 更新帖子状态（状态转换逻辑在 Controller 中判断，这里只负责简单的状态赋值和保存）
     */
    public Post updatePostStatus(String postId, String status) {
        Post post = getPostById(postId);
        post.setStatus(status);
        return postRepository.save(post);
    }

    /**
     * 删除帖子（逻辑删除，即状态设为 Deleted），在 Controller 中判断权限后调用本方法
     */
    public void deletePost(String postId, Long userId, boolean isAdmin) {
        Post post = getPostById(postId);
        // 这里假定 Controller 已经判断好了权限和状态转换的合理性
        post.setStatus("Deleted");
        postRepository.save(post);
    }

    /**
     * 为帖子添加一级回复。
     * 假定在调用此方法前，Controller 已验证可添加回复的条件（帖子状态等）。
     */
    public void addReplyToPost(String postId, Post.PostReply reply) {
        Post post = getPostById(postId);
        post.getPostReplies().add(reply);
        postRepository.save(post);
    }

    /**
     * 为指定一级回复添加子回复。
     * replyIndex 为一级回复在列表中的下标，由 Controller 确保 index 合法性。
     */
    public void addSubReplyToReply(String postId, int replyIndex, Post.PostReply.SubReply subReply) {
        Post post = getPostById(postId);
        List<Post.PostReply> replies = post.getPostReplies();

        if (replyIndex < 0 || replyIndex >= replies.size()) {
            throw new RuntimeException("Reply not found at index: " + replyIndex);
        }

        Post.PostReply parentReply = replies.get(replyIndex);
        parentReply.getSubReplies().add(subReply);
        postRepository.save(post);
    }

    /**
     * 逻辑删除回复（isActive = false）。
     * 在调用此方法前，Controller 已验证权限（拥有者或管理员）。
     */
    public void deleteReply(String postId, int replyIndex, Long userId, boolean isAdmin) {
        Post post = getPostById(postId);
        List<Post.PostReply> replies = post.getPostReplies();

        if (replyIndex < 0 || replyIndex >= replies.size()) {
            throw new RuntimeException("Reply not found at index: " + replyIndex);
        }

        // 设置此回复的 isActive = false
        Post.PostReply reply = replies.get(replyIndex);
        reply.setIsActive(false);

        // 同步保存到数据库
        postRepository.save(post);
    }


    public List<Post> findAll() {
        return postRepository.findAll();
    }


    public Post updatePostFields(String postId, Map<String, Object> updates, Long userId, boolean isAdmin) {
        Post post = getPostById(postId);

        // 权限校验：只能修改自己的帖子，除非是管理员
        if (!isAdmin && !post.getUserId().equals(userId)) {
            throw new RuntimeException("You do not have permission to update this post.");
        }

        // 动态更新字段
        updates.forEach((key, value) -> {
            try {
                // 使用反射获取字段
                Field field = Post.class.getDeclaredField(key);
                field.setAccessible(true); // 允许访问私有字段
                field.set(post, value);    // 设置新的值
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalArgumentException("Invalid field: " + key, e);
            }
        });

        post.setDateModified(new Date()); // 更新修改时间
        return postRepository.save(post); // 保存更新后的帖子
    }
}