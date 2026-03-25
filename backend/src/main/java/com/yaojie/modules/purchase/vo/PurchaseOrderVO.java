package com.yaojie.modules.purchase.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PurchaseOrderVO {

    private Long id;
    private String orderNo;
    private Long supplierId;
    private String supplierName;
    private Long operatorId;
    private String operatorName;
    private BigDecimal totalAmount;
    private String status;
    private String remark;
    private LocalDateTime createdAt;
    private List<PurchaseOrderItemVO> items;
}
