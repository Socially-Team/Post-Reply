package com.example.postreply.controller;

import com.example.postreply.model.Post;
import com.example.postreply.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.List;

//@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/posts/{postId}/replies")
public class ReplyController {

    @Autowired
    private PostService postService;

    /**
     * 添加一级回复到帖子
     */
    @PostMapping
    public ResponseEntity<String> addReply(@PathVariable String postId,
                                           @RequestBody Post.PostReply reply) {
        Long userId = getUserIdFromJWT();
        Post post = postService.getPostById(postId);

        // 检查是否可添加回复
        if (!canAddReply(post)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot add reply: Post not in a state that allows replies.");
        }

        reply.setUserId(userId);
        postService.addReplyToPost(postId, reply);
        return ResponseEntity.ok("Reply added successfully");
    }

    /**
     * 添加子回复到指定一级回复
     */
    @PostMapping("/{replyIndex}/subreplies")
    public ResponseEntity<String> addSubReply(@PathVariable String postId,
                                              @PathVariable int replyIndex,
                                              @RequestBody Post.PostReply.SubReply subReply) {
        Long userId = getUserIdFromJWT();
        Post post = postService.getPostById(postId);

        // 检查是否可添加子回复
        if (!canAddReply(post)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot add sub-reply: Post not in a state that allows replies.");
        }

        subReply.setUserId(userId);
        postService.addSubReplyToReply(postId, replyIndex, subReply);
        return ResponseEntity.ok("Sub-reply added successfully");
    }

    /**
     * 逻辑删除回复（isActive = false）
     */
    @DeleteMapping("/{replyIndex}")
    public ResponseEntity<String> deleteReply(@PathVariable String postId,
                                              @PathVariable int replyIndex) {
        Long userId = getUserIdFromJWT();
        boolean isAdmin = checkIfAdminRole();

        Post post = postService.getPostById(postId);

        // 检查是否有权限删除回复（管理员或帖子拥有者）
        if (!canDeleteReply(post, userId, isAdmin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No permission to delete this reply.");
        }

        postService.deleteReply(postId, replyIndex, userId, isAdmin);
        return ResponseEntity.ok("Reply deleted successfully");
    }

    // ========== 私有辅助方法 ==========

    private Long getUserIdFromJWT() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Long) authentication.getPrincipal();
    }

    private boolean checkIfAdminRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    /**
     * 检查帖子是否允许添加回复：
     * - 帖子状态应为 Published
     * - 帖子未被归档（isArchived = false）
     * 根据你的业务逻辑适当调整判定条件
     */
    private boolean canAddReply(Post post) {
        return "Published".equals(post.getStatus()) && Boolean.FALSE.equals(post.getIsArchived());
    }

    /**
     * 检查用户是否能删除回复：
     * - 必须是管理员或帖子拥有者
     */
    private boolean canDeleteReply(Post post, Long userId, boolean isAdmin) {
        return isAdmin || userId.equals(post.getUserId());
    }
}