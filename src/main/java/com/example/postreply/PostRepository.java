package com.example.postreply;

import com.example.postreply.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {

    /**
     * 根据帖子状态查询帖子
     * @param status 帖子状态（如: Published, Hidden, Banned, Deleted）
     * @return List<Post>
     */
    List<Post> findByStatus(String status);

    /**
     * 根据用户ID查询帖子
     * @param userId 用户ID
     * @return List<Post>
     */
    List<Post> findByUserId(Long userId);

    /**
     * 查找所有未归档的帖子
     * @param isArchived 是否归档
     * @return List<Post>
     */
    List<Post> findByIsArchivedFalse();
}