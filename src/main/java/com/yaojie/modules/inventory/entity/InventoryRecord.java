package com.yaojie.modules.inventory.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InventoryRecord {

    private Long id;
    private Long medicineId;
    private String batchNo;
    private String changeType;
    private Integer changeQuantity;
    private Integer beforeQuantity;
    private Integer afterQuantity;
    private String sourceType;
    private Long sourceId;
    private Long operatorId;
    private String remark;
    private LocalDateTime createdAt;
}
