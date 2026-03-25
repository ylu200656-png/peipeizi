package com.yaojie.modules.inventory.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Inventory {

    private Long id;
    private Long medicineId;
    private String batchNo;
    private Integer currentQuantity;
    private Integer lockedQuantity;
    private LocalDate productionDate;
    private LocalDate expiryDate;
    private LocalDateTime lastInboundTime;
    private LocalDateTime lastOutboundTime;
    private LocalDateTime updatedAt;
}
