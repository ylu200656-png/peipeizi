package com.yaojie.modules.stats.service.impl;

import com.yaojie.modules.stats.mapper.StatsMapper;
import com.yaojie.modules.stats.service.StatsService;
import com.yaojie.modules.stats.vo.DashboardOverviewVO;
import com.yaojie.modules.warning.mapper.WarningRecordMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StatsServiceImpl implements StatsService {

    private final StatsMapper statsMapper;
    private final WarningRecordMapper warningRecordMapper;

    public StatsServiceImpl(StatsMapper statsMapper, WarningRecordMapper warningRecordMapper) {
        this.statsMapper = statsMapper;
        this.warningRecordMapper = warningRecordMapper;
    }

    @Override
    public DashboardOverviewVO getDashboardOverview() {
        DashboardOverviewVO overview = new DashboardOverviewVO();
        overview.setMedicineCount(defaultInt(statsMapper.countMedicines()));
        overview.setSupplierCount(defaultInt(statsMapper.countSuppliers()));
        overview.setInventoryBatchCount(defaultInt(statsMapper.countInventoryBatches()));
        overview.setAvailableStockTotal(defaultInt(statsMapper.sumAvailableStock()));
        overview.setOpenWarningCount(defaultInt(statsMapper.countOpenWarnings()));
        overview.setTodayPurchaseAmount(defaultDecimal(statsMapper.sumTodayPurchaseAmount()));
        overview.setTodaySaleAmount(defaultDecimal(statsMapper.sumTodaySaleAmount()));
        overview.setLatestWarnings(warningRecordMapper.selectLatestList(5));
        return overview;
    }

    private Integer defaultInt(Integer value) {
        return value == null ? 0 : value;
    }

    private BigDecimal defaultDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
