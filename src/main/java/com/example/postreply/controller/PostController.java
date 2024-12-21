package com.example.postreply.controller;

import com.example.postreply.model.Post;
import com.example.postreply.security.UserPrinciple;
import com.example.postreply.service.PostService;
import com.example.postreply.AOP.Exceptions.*;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.example.postreply.security.JwtUtil;


import java.util.List;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/posts")
public class PostController {
    private JwtUtil jwtUtil;

    @Autowired
    public void setJwtUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Autowired
    private PostService postService;

    // 创建新帖子（未发布状态），由用户创建
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        UserPrinciple userPrinciple = getUserIdFromJWT();
        Long userId = userPrinciple.getUserId();
        String username = userPrinciple.getUsername(); // 从 UserPrinciple 获取用户名

        post.setUserId(userId);
        post.setUsername(username); // 设置用户名
        post.setStatus("Unpublished"); // 初始状态为 Unpublished

        return ResponseEntity.ok(postService.createPost(post));
    }

    @GetMapping
    public ResponseEntity<List<Post>> getVisiblePosts() {

        UserPrinciple userPrinciple = getUserIdFromJWT();
        Long userId = userPrinciple.getUserId();
        boolean isAdmin = checkIfAdminRole();
        boolean isSuperAdmin = checkIfSuperAdminRole(); // 若已有superAdmin逻辑则可利用，无则可省略此行

        // 从数据库获取所有帖子
        List<Post> allPosts = postService.findAll();

        // 使用canViewPost(post)过滤用户可见的帖子
        List<Post> visiblePosts = allPosts.stream()
                .filter(this::canViewPost) // canViewPost是你之前实现的私有方法，用于检查单个帖子可见性
                .collect(Collectors.toList());

        return ResponseEntity.ok(visiblePosts);
    }

    // 获取所有已发布的帖子
    @GetMapping("/published")
    public ResponseEntity<?> getPublishedPosts() {
        // 不需要鉴权过滤，这里只是示例（实际代码中可能已要求必须authenticated()）
        return ResponseEntity.ok(postService.getPublishedPosts());
    }

    // 根据ID获取帖子（根据状态和当前用户角色决定是否可见）
    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostById(@PathVariable String postId) {
        Post post = postService.getPostById(postId);
        if (!canViewPost(post)) {
            // 如果当前用户无权查看（根据状态和角色判断）
            throw new ForbiddenException("Access denied!");
        }
        return ResponseEntity.ok(post);
    }

    // 更新帖子状态，需要根据状态、角色和规则判断
    @PutMapping("/{postId}/status")
    public ResponseEntity<Post> updatePostStatus(@PathVariable String postId, @RequestParam String status) {
        UserPrinciple userPrinciple = getUserIdFromJWT();
        Long currentUserId = userPrinciple.getUserId();
        boolean isAdmin = checkIfAdminRole();
        boolean isSuperAdmin = checkIfSuperAdminRole(); // 假设需要的话，可实现checkIfSuperAdminRole

        Post post = postService.getPostById(postId);
        String currentStatus = post.getStatus();
        Long ownerId = post.getUserId();

        if (!canChangeStatus(currentStatus, status, currentUserId, ownerId, isAdmin, isSuperAdmin)) {
            throw new ForbiddenException("Access denied!");
        }

        post.setStatus(status);
        postService.savePost(post);
        return ResponseEntity.ok(post);
    }

    // 删除帖子（实际上是将帖子状态变为Deleted）
//    @DeleteMapping("/{postId}")
//    public ResponseEntity<Void> deletePost(@PathVariable String postId) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Long currentUserId = getUserIdFromJWT();
//        boolean isAdmin = checkIfAdminRole();
//
//        Post post = postService.getPostById(postId);
//        // 删除规则：已发布/隐藏/屏蔽的帖子可被拥有者或管理员删除（转为Deleted）
//        // 未发布则可以直接删除为Deleted
//        // 此处简化逻辑，实际可根据需求细分
//
//        // 判断能否从当前状态转为Deleted
//        if (!canChangeStatus(post.getStatus(), "Deleted", currentUserId, post.getUserId(), isAdmin, false)) {
//            return ResponseEntity.status(403).build();
//        }
//
//        post.setStatus("Deleted");
//        postService.savePost(post);
//        return ResponseEntity.noContent().build();
//    }
    @PatchMapping("api/update/{postId}")
    public ResponseEntity<Post> updatePostPartially(
            @PathVariable String postId,
            @RequestBody Map<String, Object> updates) {

        UserPrinciple userPrinciple = getUserIdFromJWT();
        Long userId = userPrinciple.getUserId();
        boolean isAdmin = checkIfAdminRole();

        Post updatedPost = postService.updatePostFields(postId, updates, userId, isAdmin);
        return ResponseEntity.ok(updatedPost);
    }



    @GetMapping("/unpublished-hidden")
    public ResponseEntity<List<Post>> getUnpublishedAndHiddenPosts() {
        UserPrinciple userPrinciple = getUserIdFromJWT();
        Long currentUserId = userPrinciple.getUserId();
        boolean isAdmin = checkIfAdminRole();

        // 从数据库获取所有帖子
        List<Post> allPosts = postService.findAll();

        // 过滤出当前用户的 Unpublished 和 Hidden 状态帖子，或管理员的所有帖子
        List<Post> filteredPosts = allPosts.stream()
                .filter(post -> {
                    if (isAdmin) {
                        // 管理员可以查看所有未发布和隐藏的帖子
                        return "Unpublished".equals(post.getStatus()) || "Hidden".equals(post.getStatus());
                    } else {
                        // 普通用户只能查看自己未发布和隐藏的帖子
                        return (currentUserId.equals(post.getUserId()) &&
                                ("Unpublished".equals(post.getStatus()) || "Hidden".equals(post.getStatus())));
                    }
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(filteredPosts);
    }


    //返回所有自己发的帖子
    @GetMapping("/my-posts")
    public ResponseEntity<List<Post>> getMyPosts() {
        UserPrinciple userPrinciple = getUserIdFromJWT();
        Long currentUserId = userPrinciple.getUserId();

        // 从数据库获取所有帖子并过滤当前用户创建的帖子
        List<Post> myPosts = postService.findAll().stream()
                .filter(post -> currentUserId.equals(post.getUserId()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(myPosts);
    }

    //获取当前用户id
    @GetMapping("/current-user-id")
    public ResponseEntity<Long> getCurrentUserId() {
        // 从认证上下文中获取用户 ID
        UserPrinciple userPrinciple = getUserIdFromJWT();
        Long userId = userPrinciple.getUserId();

        // 返回用户 ID
        return ResponseEntity.ok(userId);
    }

    //获取指定帖子的userid
    @GetMapping("/{postId}/user-id")
    public ResponseEntity<Long> getPostUserId(@PathVariable String postId) {
        Post post = postService.getPostById(postId);

        // 检查帖子是否存在
        if (post == null) {
            throw new NotFoundException("Post not found!");
        }

        // 返回帖子的 userId
        return ResponseEntity.ok(post.getUserId());
    }


    @GetMapping("/top-3-posts")
    public ResponseEntity<List<Post>> getTop3PublishedPosts() {
        UserPrinciple userPrinciple = getUserIdFromJWT();
        Long currentUserId = userPrinciple.getUserId();

        // 获取当前用户的所有帖子
        List<Post> userPosts = postService.findAll().stream()
                .filter(post -> post.getUserId().equals(currentUserId) && "Published".equals(post.getStatus()))
                .collect(Collectors.toList());

        // 按回复数量排序并取前 3 个帖子
        List<Post> topPosts = userPosts.stream()
                .sorted((post1, post2) -> Integer.compare(post2.getPostReplies().size(), post1.getPostReplies().size()))
                .limit(3)
                .collect(Collectors.toList());

        return ResponseEntity.ok(topPosts);
    }

    @GetMapping("/unpublished-posts")
    public ResponseEntity<List<Post>> getUnpublishedPosts() {
        UserPrinciple userPrinciple = getUserIdFromJWT();
        Long currentUserId = userPrinciple.getUserId();

        // 获取当前用户的所有未发布帖子
        List<Post> unpublishedPosts = postService.findAll().stream()
                .filter(post -> post.getUserId().equals(currentUserId) && "Unpublished".equals(post.getStatus()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(unpublishedPosts);
    }

    // ========== 私有辅助方法 ==========

    private UserPrinciple getUserIdFromJWT() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserPrinciple) authentication.getPrincipal();
    }

//    private boolean checkIfAdminRole() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
//    }

    private boolean checkIfAdminRole() {
        try {
            // 获取当前 Authentication 对象
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // 检查 authentication 是否为空
            if (authentication == null || !(authentication.getPrincipal() instanceof UserPrinciple)) {
                System.err.println("Authentication 为空或 Principal 非 UserPrinciple 类型");
                return false;
            }

            // 从 Principal 中获取 UserPrinciple
            UserPrinciple principal = (UserPrinciple) authentication.getPrincipal();

            // 获取 role 并检查是否为 ADMIN
            String role = principal.getRole();
            return "ADMIN".equals(role);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // 如果有 super admin 角色，则实现此方法，如无则可忽略
    private boolean checkIfSuperAdminRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN"));
    }

    // 判断用户是否可以查看帖子
    private boolean canViewPost(Post post) {
        UserPrinciple userPrinciple = getUserIdFromJWT();
        Long currentUserId = userPrinciple.getUserId();
//        Long currentUserId = getUserIdFromJWT();
        boolean isAdmin = checkIfAdminRole();

        switch (post.getStatus()) {
            case "Unpublished":
                // 仅帖子拥有者可见
                return currentUserId.equals(post.getUserId());
            case "Published":
                // 所有人可见
                return true;
            case "Hidden":
                // 仅帖子拥有者可见
                return currentUserId.equals(post.getUserId());
            case "Banned":
                // 管理员和帖子拥有者可见
                return isAdmin || currentUserId.equals(post.getUserId());
            case "Deleted":
                // 管理员和帖子拥有者可见
                return isAdmin || currentUserId.equals(post.getUserId());
            default:
                return false;
        }
    }

    // 判断状态转换是否可行
    private boolean canChangeStatus(String currentStatus, String newStatus,
                                    Long currentUserId, Long ownerId,
                                    boolean isAdmin, boolean isSuperAdmin) {
        // 如果 superAdmin 拥有和 admin相同甚至更高的权限，可在此添加额外判断
        // 简化逻辑：superAdmin具备admin权限
        if (isSuperAdmin) {
            isAdmin = true;
        }

        switch (currentStatus) {
            case "Unpublished":
                // 未发布 → 已发布(只有拥有者)
                return "Published".equals(newStatus) && currentUserId.equals(ownerId);

            case "Published":
                // 已发布 → 隐藏(拥有者)
                if ("Hidden".equals(newStatus) && currentUserId.equals(ownerId)) return true;
                // 已发布 → 屏蔽(Banned)(管理员)
                if ("Banned".equals(newStatus) && isAdmin) return true;
                // 已发布 → 删除(Deleted)(拥有者)
                if ("Deleted".equals(newStatus) && (currentUserId.equals(ownerId))) return true;
                return false;

            case "Hidden":
                // 隐藏 → 已发布(拥有者)
                return "Published".equals(newStatus) && currentUserId.equals(ownerId);

            case "Banned":
                // 屏蔽(Banned) → 已发布(Published)(管理员)
                return "Published".equals(newStatus) && isAdmin;

            case "Deleted":
                // 删除(Deleted) → 已发布(Published)(管理员可恢复)
                return "Published".equals(newStatus) && isAdmin;

            default:
                return false;
        }
    }
}