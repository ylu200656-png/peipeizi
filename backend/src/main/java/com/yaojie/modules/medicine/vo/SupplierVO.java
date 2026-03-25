package com.yaojie.modules.medicine.vo;

import lombok.Data;

@Data
public class SupplierVO {

    private Long id;
    private String supplierName;
    private String contactPerson;
    private String phone;
    private String address;
    private Integer status;
}
