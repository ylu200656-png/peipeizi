package com.yaojie.modules.sale.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SaleOrder {

    private Long id;
    private String orderNo;
    private Long operatorId;
    private BigDecimal totalAmount;
    private String status;
    private String remark;
    private LocalDateTime createdAt;
}
