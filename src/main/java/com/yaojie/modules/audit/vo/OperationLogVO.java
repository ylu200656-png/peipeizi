package com.yaojie.modules.audit.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperationLogVO {

    private Long id;
    private Long userId;
    private String username;
    private String realName;
    private String moduleName;
    private String operationType;
    private String businessNo;
    private String content;
    private String ip;
    private LocalDateTime createdAt;
}
