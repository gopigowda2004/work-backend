package com.workloghub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkLogRequest {
    private String title;
    private String content;
    private String technologies;
}
