package com.workloghub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkLogResponse {
    private Long id;
    private String title;
    private String content;
    private String technologies;
    private UserResponse author;
    private Integer viewCount;
    private Integer likeCount;
    private Boolean isLikedByCurrentUser;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
