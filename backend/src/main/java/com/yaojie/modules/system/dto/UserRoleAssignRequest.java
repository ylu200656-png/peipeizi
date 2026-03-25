package com.yaojie.modules.system.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserRoleAssignRequest {

    private List<Long> roleIds;
}
