package com.yaojie.modules.system.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysUser {

    private Long id;
    private String username;
    private String passwordHash;
    private String realName;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
