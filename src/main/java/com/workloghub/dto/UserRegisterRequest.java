package com.workloghub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequest {
    private String email;
    private String password;
    private String fullName;
    private String jobTitle;
    private String company;
    private Boolean isJunior = false;
    private String bio;
}
