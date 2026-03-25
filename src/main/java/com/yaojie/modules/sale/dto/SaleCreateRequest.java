package com.yaojie.modules.sale.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class SaleCreateRequest {

    private String remark;

    @Valid
    @NotEmpty(message = "items must not be empty")
    private List<SaleCreateItemRequest> items;
}
