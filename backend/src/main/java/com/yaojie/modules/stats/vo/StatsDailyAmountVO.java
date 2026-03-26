package com.yaojie.modules.stats.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class StatsDailyAmountVO {

    private LocalDate statDate;
    private BigDecimal totalAmount;
    private Integer orderCount;
}
