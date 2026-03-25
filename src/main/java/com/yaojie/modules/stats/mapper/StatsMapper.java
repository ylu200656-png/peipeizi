package com.yaojie.modules.stats.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;

@Mapper
public interface StatsMapper {

    Integer countMedicines();

    Integer countSuppliers();

    Integer countInventoryBatches();

    Integer sumAvailableStock();

    Integer countOpenWarnings();

    BigDecimal sumTodayPurchaseAmount();

    BigDecimal sumTodaySaleAmount();
}
