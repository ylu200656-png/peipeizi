package com.yaojie.modules.stats.vo;

import com.yaojie.modules.warning.vo.WarningRecordVO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DashboardOverviewVO {

    private Integer medicineCount;
    private Integer supplierCount;
    private Integer inventoryBatchCount;
    private Integer availableStockTotal;
    private Integer openWarningCount;
    private BigDecimal todayPurchaseAmount;
    private BigDecimal todaySaleAmount;
    private List<WarningRecordVO> latestWarnings;
}
