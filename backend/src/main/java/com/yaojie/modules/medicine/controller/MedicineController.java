package com.yaojie.modules.medicine.controller;

import com.yaojie.common.result.ApiResponse;
import com.yaojie.modules.medicine.dto.MedicineCreateRequest;
import com.yaojie.modules.medicine.dto.MedicineQueryRequest;
import com.yaojie.modules.medicine.dto.MedicineUpdateRequest;
import com.yaojie.modules.medicine.service.MedicineService;
import com.yaojie.modules.medicine.vo.MedicineCategoryVO;
import com.yaojie.modules.medicine.vo.MedicineVO;
import com.yaojie.modules.medicine.vo.SupplierVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/medicines")
public class MedicineController {

    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','PHARMACY_MANAGER','INVENTORY_MANAGER','SALES_CLERK')")
    public ApiResponse<List<MedicineVO>> list(MedicineQueryRequest queryRequest) {
        return ApiResponse.success(medicineService.list(queryRequest));
    }

    @GetMapping("/categories")
    @PreAuthorize("hasAnyAuthority('ADMIN','PHARMACY_MANAGER','INVENTORY_MANAGER')")
    public ApiResponse<List<MedicineCategoryVO>> categoryList() {
        return ApiResponse.success(medicineService.categoryList());
    }

    @GetMapping("/suppliers")
    @PreAuthorize("hasAnyAuthority('ADMIN','PHARMACY_MANAGER','INVENTORY_MANAGER')")
    public ApiResponse<List<SupplierVO>> supplierList() {
        return ApiResponse.success(medicineService.supplierList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PHARMACY_MANAGER','INVENTORY_MANAGER')")
    public ApiResponse<MedicineVO> getById(@PathVariable Long id) {
        return ApiResponse.success(medicineService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','PHARMACY_MANAGER','INVENTORY_MANAGER')")
    public ApiResponse<MedicineVO> create(@Valid @RequestBody MedicineCreateRequest request) {
        return ApiResponse.success("created", medicineService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PHARMACY_MANAGER','INVENTORY_MANAGER')")
    public ApiResponse<MedicineVO> update(@PathVariable Long id, @Valid @RequestBody MedicineUpdateRequest request) {
        return ApiResponse.success("updated", medicineService.update(id, request));
    }
}
