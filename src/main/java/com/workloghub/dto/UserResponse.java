package com.workloghub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String fullName;
    private String jobTitle;
    private String company;
    private String bio;
    private String profileImageUrl;
    private Boolean isJunior;
    private LocalDateTime createdAt;
}
