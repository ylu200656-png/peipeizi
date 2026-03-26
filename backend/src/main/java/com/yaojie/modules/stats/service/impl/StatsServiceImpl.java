package com.yaojie.modules.stats.service.impl;

import com.yaojie.modules.stats.mapper.StatsMapper;
import com.yaojie.modules.stats.service.StatsService;
import com.yaojie.modules.stats.vo.DashboardOverviewVO;
import com.yaojie.modules.stats.vo.StatsDailyAmountVO;
import com.yaojie.modules.stats.vo.StatsInventoryCategoryVO;
import com.yaojie.modules.stats.vo.StatsTrendPointVO;
import com.yaojie.modules.stats.vo.StatsWarningTypeVO;
import com.yaojie.modules.warning.mapper.WarningRecordMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsServiceImpl implements StatsService {

    private static final int DEFAULT_TREND_DAYS = 7;
    private static final int MAX_TREND_DAYS = 30;

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

    @Override
    public List<StatsTrendPointVO> getTradeTrend(int days) {
        int safeDays = normalizeDays(days);
        LocalDate startDate = LocalDate.now().minusDays(safeDays - 1L);
        Map<LocalDate, StatsTrendPointVO> points = new LinkedHashMap<>();

        for (int i = 0; i < safeDays; i++) {
            LocalDate statDate = startDate.plusDays(i);
            StatsTrendPointVO point = new StatsTrendPointVO();
            point.setStatDate(statDate);
            point.setPurchaseAmount(BigDecimal.ZERO);
            point.setSaleAmount(BigDecimal.ZERO);
            point.setPurchaseOrderCount(0);
            point.setSaleOrderCount(0);
            points.put(statDate, point);
        }

        mergePurchaseTrend(points, statsMapper.selectPurchaseTrend(startDate));
        mergeSaleTrend(points, statsMapper.selectSaleTrend(startDate));

        return new ArrayList<>(points.values());
    }

    @Override
    public List<StatsInventoryCategoryVO> getInventoryCategoryStats() {
        return statsMapper.selectInventoryCategoryStats();
    }

    @Override
    public List<StatsWarningTypeVO> getWarningTypeStats() {
        return statsMapper.selectOpenWarningTypeStats();
    }

    private void mergePurchaseTrend(Map<LocalDate, StatsTrendPointVO> points, List<StatsDailyAmountVO> rows) {
        for (StatsDailyAmountVO row : rows) {
            StatsTrendPointVO point = points.get(row.getStatDate());
            if (point == null) {
                continue;
            }
            point.setPurchaseAmount(defaultDecimal(row.getTotalAmount()));
            point.setPurchaseOrderCount(defaultInt(row.getOrderCount()));
        }
    }

    private void mergeSaleTrend(Map<LocalDate, StatsTrendPointVO> points, List<StatsDailyAmountVO> rows) {
        for (StatsDailyAmountVO row : rows) {
            StatsTrendPointVO point = points.get(row.getStatDate());
            if (point == null) {
                continue;
            }
            point.setSaleAmount(defaultDecimal(row.getTotalAmount()));
            point.setSaleOrderCount(defaultInt(row.getOrderCount()));
        }
    }

    private int normalizeDays(int days) {
        if (days <= 0) {
            return DEFAULT_TREND_DAYS;
        }
        return Math.min(days, MAX_TREND_DAYS);
    }

    private Integer defaultInt(Integer value) {
        return value == null ? 0 : value;
    }

    private BigDecimal defaultDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
