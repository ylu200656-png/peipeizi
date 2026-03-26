package com.yaojie.modules.stats.mapper;

import com.yaojie.modules.stats.vo.StatsDailyAmountVO;
import com.yaojie.modules.stats.vo.StatsInventoryCategoryVO;
import com.yaojie.modules.stats.vo.StatsWarningTypeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface StatsMapper {

    Integer countMedicines();

    Integer countSuppliers();

    Integer countInventoryBatches();

    Integer sumAvailableStock();

    Integer countOpenWarnings();

    BigDecimal sumTodayPurchaseAmount();

    BigDecimal sumTodaySaleAmount();

    List<StatsDailyAmountVO> selectPurchaseTrend(@Param("startDate") LocalDate startDate);

    List<StatsDailyAmountVO> selectSaleTrend(@Param("startDate") LocalDate startDate);

    List<StatsInventoryCategoryVO> selectInventoryCategoryStats();

    List<StatsWarningTypeVO> selectOpenWarningTypeStats();
}
