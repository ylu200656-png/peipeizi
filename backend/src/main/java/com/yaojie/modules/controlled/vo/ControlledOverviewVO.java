package com.yaojie.modules.controlled.vo;

import lombok.Data;

@Data
public class ControlledOverviewVO {

    private Integer medicineCount;
    private Integer availableStockTotal;
    private Integer openWarningCount;
    private Integer expiringBatchCount;
}
