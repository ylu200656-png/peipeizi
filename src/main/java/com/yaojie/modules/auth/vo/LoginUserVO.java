package com.yaojie.modules.auth.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LoginUserVO {

    private Long id;
    private String username;
    private String realName;
    private List<String> roles;
}
