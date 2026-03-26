package com.yaojie.modules.medicine.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SupplierSaveRequest {

    @NotBlank(message = "supplierName is required")
    private String supplierName;

    private String contactPerson;

    private String phone;

    private String address;

    private Integer status;
}
