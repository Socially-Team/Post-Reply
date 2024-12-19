package com.example.postreply.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Document(collection = "posts")
public class Post {

    @Id
    private String postId;  // 使用 postId 作为 MongoDB 的主键 _id

    // 其他字段
    private Long userId;
    private String title;
    private String content;
    private Boolean isArchived = false;
    private String status;
    private Date dateCreated = new Date();
    private Date dateModified = new Date();
    private List<String> images = new ArrayList<>();
    private List<String> attachments = new ArrayList<>();
    private List<PostReply> postReplies = new ArrayList<>();

    // Getters and Setters for Post class
    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(Boolean isArchived) {
        this.isArchived = isArchived;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    public List<PostReply> getPostReplies() {
        return postReplies;
    }

    public void setPostReplies(List<PostReply> postReplies) {
        this.postReplies = postReplies;
    }

    // Nested Class: PostReply
    public static class PostReply {
        private Long userId;        // ID of the user who replied
        private String comment;     // Content of the reply
        private Boolean isActive = true; // Logical delete flag
        private Date dateCreated = new Date(); // Reply creation date
        private List<SubReply> subReplies = new ArrayList<>(); // List of sub-replies

        // Getters and Setters for PostReply class
        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public Boolean getIsActive() {
            return isActive;
        }

        public void setIsActive(Boolean isActive) {
            this.isActive = isActive;
        }

        public Date getDateCreated() {
            return dateCreated;
        }

        public void setDateCreated(Date dateCreated) {
            this.dateCreated = dateCreated;
        }

        public List<SubReply> getSubReplies() {
            return subReplies;
        }

        public void setSubReplies(List<SubReply> subReplies) {
            this.subReplies = subReplies;
        }

        // Nested Class: SubReply
        public static class SubReply {
            private Long userId;    // ID of the user who replied
            private String comment; // Content of the sub-reply
            private Boolean isActive = true; // Logical delete flag
            private Date dateCreated = new Date(); // Sub-reply creation date

            // Getters and Setters for SubReply class
            public Long getUserId() {
                return userId;
            }

            public void setUserId(Long userId) {
                this.userId = userId;
            }

            public String getComment() {
                return comment;
            }

            public void setComment(String comment) {
                this.comment = comment;
            }

            public Boolean getIsActive() {
                return isActive;
            }

            public void setIsActive(Boolean isActive) {
                this.isActive = isActive;
            }

            public Date getDateCreated() {
                return dateCreated;
            }

            public void setDateCreated(Date dateCreated) {
                this.dateCreated = dateCreated;
            }
        }
    }
}