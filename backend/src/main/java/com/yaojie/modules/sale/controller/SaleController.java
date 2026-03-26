package com.yaojie.modules.sale.controller;

import com.yaojie.common.result.ApiResponse;
import com.yaojie.modules.sale.dto.SaleCreateRequest;
import com.yaojie.modules.sale.service.SaleService;
import com.yaojie.modules.sale.vo.SaleOrderVO;
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
@RequestMapping("/api/v1/sales")
@PreAuthorize("hasAnyAuthority('ADMIN','PHARMACY_MANAGER','SALES_CLERK')")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping
    public ApiResponse<List<SaleOrderVO>> list() {
        return ApiResponse.success(saleService.list());
    }

    @GetMapping("/{id}")
    public ApiResponse<SaleOrderVO> getById(@PathVariable Long id) {
        return ApiResponse.success(saleService.getById(id));
    }

    @PostMapping
    public ApiResponse<SaleOrderVO> create(
        @Valid @RequestBody SaleCreateRequest request,
        Authentication authentication,
        HttpServletRequest httpServletRequest
    ) {
        return ApiResponse.success(
            "created",
            saleService.create(request, authentication.getName(), httpServletRequest.getRemoteAddr())
        );
    }
}
