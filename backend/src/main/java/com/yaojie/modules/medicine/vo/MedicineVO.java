package com.yaojie.modules.medicine.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MedicineVO {

    private Long id;
    private String medicineCode;
    private String medicineName;
    private Long categoryId;
    private String categoryName;
    private String specification;
    private String unit;
    private String manufacturer;
    private Long supplierId;
    private String supplierName;
    private BigDecimal purchasePrice;
    private BigDecimal salePrice;
    private Integer safeStock;
    private Integer expiryWarningDays;
    private Integer isControlled;
    private Integer status;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
