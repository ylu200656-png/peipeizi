package com.yaojie.modules.purchase.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PurchaseOrderItem {

    private Long id;
    private Long purchaseOrderId;
    private Long medicineId;
    private String batchNo;
    private Integer quantity;
    private BigDecimal purchasePrice;
    private LocalDate productionDate;
    private LocalDate expiryDate;
    private BigDecimal subtotal;
    private LocalDateTime createdAt;
}
