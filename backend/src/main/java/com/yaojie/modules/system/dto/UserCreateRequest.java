package com.yaojie.modules.system.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserCreateRequest {

    private String username;
    private String realName;
    private String password;
    private Integer status;
    private List<Long> roleIds;
}
