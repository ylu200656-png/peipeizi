package com.yaojie.modules.sale.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaleOrderItemVO {

    private Long id;
    private Long medicineId;
    private String medicineName;
    private String batchNo;
    private Integer quantity;
    private BigDecimal salePrice;
    private BigDecimal subtotal;
}
