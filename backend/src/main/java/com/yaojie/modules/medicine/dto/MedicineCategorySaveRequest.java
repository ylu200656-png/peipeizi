package com.yaojie.modules.medicine.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MedicineCategorySaveRequest {

    @NotBlank(message = "categoryName is required")
    private String categoryName;

    @NotBlank(message = "categoryCode is required")
    private String categoryCode;

    private String remark;
}
