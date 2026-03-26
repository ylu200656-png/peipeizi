package com.yaojie.modules.inventory.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InventoryRecordVO {

    private Long id;
    private Long medicineId;
    private String medicineCode;
    private String medicineName;
    private String batchNo;
    private String changeType;
    private Integer changeQuantity;
    private Integer beforeQuantity;
    private Integer afterQuantity;
    private String sourceType;
    private Long sourceId;
    private Long operatorId;
    private String operatorName;
    private String remark;
    private LocalDateTime createdAt;
}
