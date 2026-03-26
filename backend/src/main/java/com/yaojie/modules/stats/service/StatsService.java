package com.yaojie.modules.stats.service;

import com.yaojie.modules.stats.vo.DashboardOverviewVO;
import com.yaojie.modules.stats.vo.StatsInventoryCategoryVO;
import com.yaojie.modules.stats.vo.StatsTrendPointVO;
import com.yaojie.modules.stats.vo.StatsWarningTypeVO;

import java.util.List;

public interface StatsService {

    DashboardOverviewVO getDashboardOverview();

    List<StatsTrendPointVO> getTradeTrend(int days);

    List<StatsInventoryCategoryVO> getInventoryCategoryStats();

    List<StatsWarningTypeVO> getWarningTypeStats();
}
