package com.yaojie.modules.sale.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SaleCreateItemRequest {

    @NotNull(message = "medicineId is required")
    private Long medicineId;

    private String batchNo;

    @NotNull(message = "quantity is required")
    @Min(value = 1, message = "quantity must be >= 1")
    private Integer quantity;
}
