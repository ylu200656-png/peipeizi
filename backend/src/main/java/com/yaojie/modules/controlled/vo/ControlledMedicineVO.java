package com.yaojie.modules.controlled.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ControlledMedicineVO {

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
    private Integer status;
    private String remark;
    private Integer availableStock;
    private Integer batchCount;
    private Integer openWarningCount;
}
