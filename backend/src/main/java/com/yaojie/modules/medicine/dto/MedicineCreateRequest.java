package com.yaojie.modules.medicine.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MedicineCreateRequest {

    @NotBlank(message = "medicineCode is required")
    private String medicineCode;

    @NotBlank(message = "medicineName is required")
    private String medicineName;

    @NotNull(message = "categoryId is required")
    private Long categoryId;

    private String specification;

    @NotBlank(message = "unit is required")
    private String unit;

    private String manufacturer;

    private Long supplierId;

    @NotNull(message = "purchasePrice is required")
    private BigDecimal purchasePrice;

    @NotNull(message = "salePrice is required")
    private BigDecimal salePrice;

    @NotNull(message = "safeStock is required")
    @Min(value = 0, message = "safeStock must be >= 0")
    private Integer safeStock;

    @NotNull(message = "expiryWarningDays is required")
    @Min(value = 1, message = "expiryWarningDays must be >= 1")
    private Integer expiryWarningDays;

    @NotNull(message = "isControlled is required")
    private Integer isControlled;

    @NotNull(message = "status is required")
    private Integer status;

    private String remark;
}
