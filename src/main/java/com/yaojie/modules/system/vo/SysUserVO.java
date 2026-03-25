package com.yaojie.modules.system.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SysUserVO {

    private Long id;
    private String username;
    private String realName;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Long> roleIds;
    private List<String> roleCodes;
    private List<String> roleNames;
}
