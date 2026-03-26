package com.yaojie.modules.inventory.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class InventoryCheckCreateRequest {

    private String remark;

    @Valid
    @NotEmpty(message = "items must not be empty")
    private List<InventoryCheckCreateItemRequest> items;
}
