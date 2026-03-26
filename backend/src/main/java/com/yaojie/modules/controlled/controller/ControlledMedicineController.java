package com.yaojie.modules.controlled.controller;

import com.yaojie.common.result.ApiResponse;
import com.yaojie.modules.controlled.service.ControlledMedicineService;
import com.yaojie.modules.controlled.vo.ControlledBatchVO;
import com.yaojie.modules.controlled.vo.ControlledMedicineVO;
import com.yaojie.modules.controlled.vo.ControlledOverviewVO;
import com.yaojie.modules.controlled.vo.ControlledRecordVO;
import com.yaojie.modules.warning.vo.WarningRecordVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/controlled")
@PreAuthorize("hasAnyAuthority('ADMIN','PHARMACY_MANAGER','INVENTORY_MANAGER')")
public class ControlledMedicineController {

    private final ControlledMedicineService controlledMedicineService;

    public ControlledMedicineController(ControlledMedicineService controlledMedicineService) {
        this.controlledMedicineService = controlledMedicineService;
    }

    @GetMapping("/overview")
    public ApiResponse<ControlledOverviewVO> overview() {
        return ApiResponse.success(controlledMedicineService.getOverview());
    }

    @GetMapping("/medicines")
    public ApiResponse<List<ControlledMedicineVO>> listMedicines(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) Integer status
    ) {
        return ApiResponse.success(controlledMedicineService.listMedicines(keyword, status));
    }

    @GetMapping("/medicines/{id}/batches")
    public ApiResponse<List<ControlledBatchVO>> listBatches(@PathVariable Long id) {
        return ApiResponse.success(controlledMedicineService.listBatches(id));
    }

    @GetMapping("/medicines/{id}/records")
    public ApiResponse<List<ControlledRecordVO>> listRecords(
        @PathVariable Long id,
        @RequestParam(required = false) Integer limit
    ) {
        return ApiResponse.success(controlledMedicineService.listRecords(id, limit));
    }

    @GetMapping("/medicines/{id}/warnings")
    public ApiResponse<List<WarningRecordVO>> listWarnings(
        @PathVariable Long id,
        @RequestParam(required = false, defaultValue = "OPEN") String status
    ) {
        return ApiResponse.success(controlledMedicineService.listWarnings(id, status));
    }
}
