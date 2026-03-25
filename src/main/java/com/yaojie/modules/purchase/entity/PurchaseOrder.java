package com.yaojie.modules.purchase.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PurchaseOrder {

    private Long id;
    private String orderNo;
    private Long supplierId;
    private Long operatorId;
    private BigDecimal totalAmount;
    private String status;
    private String remark;
    private LocalDateTime createdAt;
}
