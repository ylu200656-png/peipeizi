package com.yaojie.modules.system.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysRole {

    private Long id;
    private String roleCode;
    private String roleName;
    private LocalDateTime createdAt;
}
