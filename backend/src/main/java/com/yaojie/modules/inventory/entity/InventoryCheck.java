package com.yaojie.modules.inventory.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InventoryCheck {

    private Long id;
    private String checkNo;
    private String status;
    private String remark;
    private Long createdBy;
    private Long executedBy;
    private LocalDateTime createdAt;
    private LocalDateTime executedAt;
}
