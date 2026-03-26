package com.yaojie.modules.inventory.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InventoryCheckItem {

    private Long id;
    private Long checkId;
    private Long medicineId;
    private String batchNo;
    private Integer systemQuantity;
    private Integer actualQuantity;
    private Integer differenceQuantity;
    private String reason;
    private LocalDateTime createdAt;
}
