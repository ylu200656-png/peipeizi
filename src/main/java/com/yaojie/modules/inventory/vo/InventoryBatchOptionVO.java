package com.yaojie.modules.inventory.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InventoryBatchOptionVO {

    private Long medicineId;
    private String medicineName;
    private String batchNo;
    private Integer availableQuantity;
    private LocalDate expiryDate;
    private BigDecimal salePrice;
    private Integer isControlled;
}
