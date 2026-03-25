package com.yaojie.modules.purchase.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PurchaseCreateRequest {

    @NotNull(message = "supplierId is required")
    private Long supplierId;

    private String remark;

    @Valid
    @NotEmpty(message = "items must not be empty")
    private List<PurchaseCreateItemRequest> items;
}
