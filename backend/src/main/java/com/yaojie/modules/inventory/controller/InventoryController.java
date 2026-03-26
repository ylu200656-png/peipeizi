package com.yaojie.modules.inventory.controller;

import com.yaojie.common.result.ApiResponse;
import com.yaojie.modules.inventory.dto.InventoryCheckCreateRequest;
import com.yaojie.modules.inventory.service.InventoryService;
import com.yaojie.modules.inventory.vo.InventoryBatchOptionVO;
import com.yaojie.modules.inventory.vo.InventoryCheckVO;
import com.yaojie.modules.inventory.vo.InventoryItemVO;
import com.yaojie.modules.inventory.vo.InventoryRecordVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventories")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','PHARMACY_MANAGER','INVENTORY_MANAGER')")
    public ApiResponse<List<InventoryItemVO>> list(
        @RequestParam(required = false) String keyword
    ) {
        return ApiResponse.success(inventoryService.list(keyword));
    }

    @GetMapping("/available-batches")
    @PreAuthorize("hasAnyAuthority('ADMIN','PHARMACY_MANAGER','INVENTORY_MANAGER','SALES_CLERK')")
    public ApiResponse<List<InventoryBatchOptionVO>> listAvailableBatches(
        @RequestParam Long medicineId
    ) {
        return ApiResponse.success(inventoryService.listAvailableBatches(medicineId));
    }

    @GetMapping("/records")
    @PreAuthorize("hasAnyAuthority('ADMIN','PHARMACY_MANAGER','INVENTORY_MANAGER')")
    public ApiResponse<List<InventoryRecordVO>> listRecords(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String sourceType
    ) {
        return ApiResponse.success(inventoryService.listRecords(keyword, sourceType));
    }

    @GetMapping("/checks")
    @PreAuthorize("hasAnyAuthority('ADMIN','PHARMACY_MANAGER','INVENTORY_MANAGER')")
    public ApiResponse<List<InventoryCheckVO>> listChecks(@RequestParam(required = false) String status) {
        return ApiResponse.success(inventoryService.listChecks(status));
    }

    @GetMapping("/checks/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PHARMACY_MANAGER','INVENTORY_MANAGER')")
    public ApiResponse<InventoryCheckVO> getCheck(@PathVariable Long id) {
        return ApiResponse.success(inventoryService.getCheck(id));
    }

    @PostMapping("/checks")
    @PreAuthorize("hasAnyAuthority('ADMIN','PHARMACY_MANAGER','INVENTORY_MANAGER')")
    public ApiResponse<InventoryCheckVO> createCheck(
        @Valid @RequestBody InventoryCheckCreateRequest request,
        Authentication authentication,
        HttpServletRequest httpServletRequest
    ) {
        return ApiResponse.success(
            "created",
            inventoryService.createCheck(request, authentication.getName(), httpServletRequest.getRemoteAddr())
        );
    }

    @PostMapping("/checks/{id}/execute")
    @PreAuthorize("hasAnyAuthority('ADMIN','PHARMACY_MANAGER','INVENTORY_MANAGER')")
    public ApiResponse<InventoryCheckVO> executeCheck(
        @PathVariable Long id,
        Authentication authentication,
        HttpServletRequest httpServletRequest
    ) {
        return ApiResponse.success(
            "executed",
            inventoryService.executeCheck(id, authentication.getName(), httpServletRequest.getRemoteAddr())
        );
    }
}
