package com.yaojie.modules.purchase.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PurchaseOrderItemVO {

    private Long id;
    private Long medicineId;
    private String medicineName;
    private String batchNo;
    private Integer quantity;
    private BigDecimal purchasePrice;
    private LocalDate productionDate;
    private LocalDate expiryDate;
    private BigDecimal subtotal;
}
