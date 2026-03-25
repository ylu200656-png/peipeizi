package com.yaojie.modules.purchase.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PurchaseCreateItemRequest {

    @NotNull(message = "medicineId is required")
    private Long medicineId;

    @NotBlank(message = "batchNo is required")
    private String batchNo;

    @NotNull(message = "quantity is required")
    @Min(value = 1, message = "quantity must be >= 1")
    private Integer quantity;

    @NotNull(message = "purchasePrice is required")
    @DecimalMin(value = "0.00", message = "purchasePrice must be >= 0")
    private BigDecimal purchasePrice;

    @NotNull(message = "productionDate is required")
    private LocalDate productionDate;

    @NotNull(message = "expiryDate is required")
    private LocalDate expiryDate;
}
