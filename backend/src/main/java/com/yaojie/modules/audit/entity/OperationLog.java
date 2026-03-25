package com.yaojie.modules.audit.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperationLog {

    private Long id;
    private Long userId;
    private String moduleName;
    private String operationType;
    private String businessNo;
    private String content;
    private String ip;
    private LocalDateTime createdAt;
}
