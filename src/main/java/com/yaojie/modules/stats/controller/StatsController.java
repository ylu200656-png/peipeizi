package com.yaojie.modules.stats.controller;

import com.yaojie.common.result.ApiResponse;
import com.yaojie.modules.stats.service.StatsService;
import com.yaojie.modules.stats.vo.DashboardOverviewVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
