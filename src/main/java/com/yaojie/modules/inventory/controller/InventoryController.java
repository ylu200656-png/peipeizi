package com.yaojie.modules.inventory.controller;

import com.yaojie.common.result.ApiResponse;
import com.yaojie.modules.inventory.service.InventoryService;
import com.yaojie.modules.inventory.vo.InventoryBatchOptionVO;
import com.yaojie.modules.inventory.vo.InventoryItemVO;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ApiResponse<List<InventoryItemVO>> list(
        @RequestParam(required = false) String keyword
    ) {
        return ApiResponse.success(inventoryService.list(keyword));
    }

    @GetMapping("/available-batches")
    public ApiResponse<List<InventoryBatchOptionVO>> listAvailableBatches(
        @RequestParam Long medicineId
    ) {
        return ApiResponse.success(inventoryService.listAvailableBatches(medicineId));
    }
}
