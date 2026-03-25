package com.yaojie.modules.purchase.controller;

import com.yaojie.common.result.ApiResponse;
import com.yaojie.modules.purchase.dto.PurchaseCreateRequest;
import com.yaojie.modules.purchase.service.PurchaseService;
import com.yaojie.modules.purchase.vo.PurchaseOrderVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @GetMapping
    public ApiResponse<List<PurchaseOrderVO>> list() {
        return ApiResponse.success(purchaseService.list());
    }

    @GetMapping("/{id}")
    public ApiResponse<PurchaseOrderVO> getById(@PathVariable Long id) {
        return ApiResponse.success(purchaseService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','PHARMACY_MANAGER','INVENTORY_MANAGER')")
    public ApiResponse<PurchaseOrderVO> create(
        @Valid @RequestBody PurchaseCreateRequest request,
        Authentication authentication,
        HttpServletRequest httpServletRequest
    ) {
        return ApiResponse.success(
            "created",
            purchaseService.create(request, authentication.getName(), httpServletRequest.getRemoteAddr())
        );
    }
}
