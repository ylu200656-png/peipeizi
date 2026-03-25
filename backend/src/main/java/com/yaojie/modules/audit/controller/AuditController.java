package com.yaojie.modules.audit.controller;

import com.yaojie.common.result.ApiResponse;
import com.yaojie.modules.audit.service.AuditService;
import com.yaojie.modules.audit.vo.OperationLogVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/operation-logs")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','PHARMACY_MANAGER')")
    public ApiResponse<List<OperationLogVO>> list(
        @RequestParam(required = false) String moduleName,
        @RequestParam(required = false) String operationType
    ) {
        return ApiResponse.success(auditService.listOperationLogs(moduleName, operationType));
    }
}
