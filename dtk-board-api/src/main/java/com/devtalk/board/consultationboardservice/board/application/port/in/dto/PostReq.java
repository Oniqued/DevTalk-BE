package com.devtalk.board.consultationboardservice.board.application.port.in.dto;

import com.devtalk.board.consultationboardservice.board.domain.attachedfile.AttachedFile;
import com.devtalk.board.consultationboardservice.board.domain.post.Post;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public class PostReq {
    @Getter
    @Builder
    public static class PostCreationReq{
        private String title;
        private String content;
        private Long userId;

        @Builder.Default
        private List<MultipartFile> attachedFileList = new ArrayList<>();

        public Post toEntity(String userName) {
            return Post.createPost(title, content, userId, userName);
        }
    }

    @Getter
    @Builder
    public static class PostModifyReq{
        private Long postId;
        private String title;
        private String content;
        private Long userId;

        public Post toEntity(String userName) {
            return Post.createPost(title, content, userId, userName);
        }
    }
}
