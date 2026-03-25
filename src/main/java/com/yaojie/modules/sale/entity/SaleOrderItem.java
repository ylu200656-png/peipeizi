package com.yaojie.modules.sale.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SaleOrderItem {

    private Long id;
    private Long saleOrderId;
    private Long medicineId;
    private String batchNo;
    private Integer quantity;
    private BigDecimal salePrice;
    private BigDecimal subtotal;
    private LocalDateTime createdAt;
}
