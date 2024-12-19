package com.example.postreply.service;

import com.example.postreply.PostRepository;
import com.example.postreply.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReplyService {

    @Autowired
    private PostRepository postRepository;

    /**
     * 添加一级回复到帖子
     * @param postId 帖子ID
     * @param reply 回复对象
     */
    public void addReply(String postId, Post.PostReply reply) {
        Post post = getPostById(postId);
        post.getPostReplies().add(reply);
        postRepository.save(post);
    }

    /**
     * 添加子回复到指定一级回复
     * @param postId 帖子ID
     * @param replyIndex 一级回复索引
     * @param subReply 子回复对象
     */
    public void addSubReply(String postId, int replyIndex, Post.PostReply.SubReply subReply) {
        Post post = getPostById(postId);

        // 检查回复索引是否有效
        if (replyIndex < 0 || replyIndex >= post.getPostReplies().size()) {
            throw new IndexOutOfBoundsException("Invalid reply index.");
        }

        // 添加子回复
        post.getPostReplies().get(replyIndex).getSubReplies().add(subReply);
        postRepository.save(post);
    }

    /**
     * 逻辑删除回复 (isActive = false)
     * @param postId 帖子ID
     * @param replyIndex 回复索引
     * @param userId 用户ID
     * @param isAdmin 是否为管理员
     */
    public void deleteReply(String postId, int replyIndex, Long userId, boolean isAdmin) {
        Post post = getPostById(postId);

        // 检查回复索引是否有效
        if (replyIndex < 0 || replyIndex >= post.getPostReplies().size()) {
            throw new IndexOutOfBoundsException("Invalid reply index.");
        }

        Post.PostReply reply = post.getPostReplies().get(replyIndex);

        // 验证权限：管理员或回复所有者才能删除
        if (!isAdmin && !reply.getUserId().equals(userId)) {
            throw new RuntimeException("You do not have permission to delete this reply.");
        }

        reply.setIsActive(false); // 逻辑删除
        postRepository.save(post);
    }

    /**
     * 获取帖子对象
     * @param postId 帖子ID
     * @return Post 帖子对象
     */
    private Post getPostById(String postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found."));
    }
}