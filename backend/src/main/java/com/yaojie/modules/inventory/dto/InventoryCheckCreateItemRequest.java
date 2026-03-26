package com.yaojie.modules.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventoryCheckCreateItemRequest {

    @NotNull(message = "medicineId is required")
    private Long medicineId;

    @NotBlank(message = "batchNo is required")
    private String batchNo;

    @NotNull(message = "actualQuantity is required")
    @Min(value = 0, message = "actualQuantity must be >= 0")
    private Integer actualQuantity;

    private String reason;
}
