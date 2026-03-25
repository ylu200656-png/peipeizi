package com.yaojie.modules.medicine.dto;

import lombok.Data;

@Data
public class MedicineQueryRequest {

    private String keyword;
    private Long categoryId;
    private Long supplierId;
    private Integer isControlled;
    private Integer status;
}
