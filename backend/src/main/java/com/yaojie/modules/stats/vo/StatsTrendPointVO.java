package com.yaojie.modules.stats.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class StatsTrendPointVO {

    private LocalDate statDate;
    private BigDecimal purchaseAmount;
    private BigDecimal saleAmount;
    private Integer purchaseOrderCount;
    private Integer saleOrderCount;
}
