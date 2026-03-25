package com.yaojie.modules.warning.vo;

import lombok.Data;

@Data
public class WarningSummaryVO {

    private Integer openTotal;
    private Integer lowStockCount;
    private Integer expirySoonCount;
    private Integer expiredCount;
}
