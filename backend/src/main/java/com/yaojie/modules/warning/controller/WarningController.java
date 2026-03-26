package com.yaojie.modules.warning.controller;

import com.yaojie.common.result.ApiResponse;
import com.yaojie.modules.warning.dto.WarningResolveRequest;
import com.yaojie.modules.warning.service.WarningService;
import com.yaojie.modules.warning.vo.WarningRecordVO;
import com.yaojie.modules.warning.vo.WarningSummaryVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/warnings")
@PreAuthorize("hasAnyAuthority('ADMIN','PHARMACY_MANAGER','INVENTORY_MANAGER')")
public class WarningController {

    private final WarningService warningService;

    public WarningController(WarningService warningService) {
        this.warningService = warningService;
    }

    @GetMapping
    public ApiResponse<List<WarningRecordVO>> list(
        @RequestParam(required = false) String warningType,
        @RequestParam(required = false) String status
    ) {
        return ApiResponse.success(warningService.list(warningType, status));
    }

    @GetMapping("/summary")
    public ApiResponse<WarningSummaryVO> summary() {
        return ApiResponse.success(warningService.getSummary());
    }

    @PutMapping("/{id}/resolve")
    public ApiResponse<WarningRecordVO> resolve(
        @PathVariable Long id,
        @RequestBody(required = false) WarningResolveRequest request,
        Authentication authentication,
        HttpServletRequest httpServletRequest
    ) {
        return ApiResponse.success(
            "resolved",
            warningService.resolve(id, request, authentication.getName(), httpServletRequest.getRemoteAddr())
        );
    }
}
