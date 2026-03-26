package com.yaojie.modules.stats.controller;

import com.yaojie.common.result.ApiResponse;
import com.yaojie.modules.stats.service.StatsService;
import com.yaojie.modules.stats.vo.DashboardOverviewVO;
import com.yaojie.modules.stats.vo.StatsInventoryCategoryVO;
import com.yaojie.modules.stats.vo.StatsTrendPointVO;
import com.yaojie.modules.stats.vo.StatsWarningTypeVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/overview")
    public ApiResponse<DashboardOverviewVO> overview() {
        return ApiResponse.success(statsService.getDashboardOverview());
    }

    @GetMapping("/trend")
    public ApiResponse<List<StatsTrendPointVO>> trend(@RequestParam(required = false, defaultValue = "7") int days) {
        return ApiResponse.success(statsService.getTradeTrend(days));
    }

    @GetMapping("/inventory-category")
    public ApiResponse<List<StatsInventoryCategoryVO>> inventoryCategory() {
        return ApiResponse.success(statsService.getInventoryCategoryStats());
    }

    @GetMapping("/warning-distribution")
    public ApiResponse<List<StatsWarningTypeVO>> warningDistribution() {
        return ApiResponse.success(statsService.getWarningTypeStats());
    }
}
