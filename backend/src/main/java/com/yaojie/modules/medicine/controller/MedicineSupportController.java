package com.yaojie.modules.medicine.controller;

import com.yaojie.common.result.ApiResponse;
import com.yaojie.modules.medicine.dto.MedicineCategorySaveRequest;
import com.yaojie.modules.medicine.dto.SupplierSaveRequest;
import com.yaojie.modules.medicine.service.MedicineService;
import com.yaojie.modules.medicine.vo.MedicineCategoryVO;
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
public class MedicineSupportController {

    private final MedicineService medicineService;

    public MedicineSupportController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @GetMapping("/api/v1/medicine-categories")
    @PreAuthorize("hasAnyAuthority('ADMIN','PHARMACY_MANAGER','INVENTORY_MANAGER')")
    public ApiResponse<List<MedicineCategoryVO>> listCategories() {
        return ApiResponse.success(medicineService.categoryManageList());
    }

    @PostMapping("/api/v1/medicine-categories")
    @PreAuthorize("hasAnyAuthority('ADMIN','PHARMACY_MANAGER','INVENTORY_MANAGER')")
    public ApiResponse<MedicineCategoryVO> createCategory(@Valid @RequestBody MedicineCategorySaveRequest request) {
        return ApiResponse.success("created", medicineService.createCategory(request));
    }

    @PutMapping("/api/v1/medicine-categories/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PHARMACY_MANAGER','INVENTORY_MANAGER')")
    public ApiResponse<MedicineCategoryVO> updateCategory(@PathVariable Long id, @Valid @RequestBody MedicineCategorySaveRequest request) {
        return ApiResponse.success("updated", medicineService.updateCategory(id, request));
    }

    @GetMapping("/api/v1/suppliers")
    @PreAuthorize("hasAnyAuthority('ADMIN','PHARMACY_MANAGER','INVENTORY_MANAGER')")
    public ApiResponse<List<SupplierVO>> listSuppliers() {
        return ApiResponse.success(medicineService.supplierManageList());
    }

    @PostMapping("/api/v1/suppliers")
    @PreAuthorize("hasAnyAuthority('ADMIN','PHARMACY_MANAGER','INVENTORY_MANAGER')")
    public ApiResponse<SupplierVO> createSupplier(@Valid @RequestBody SupplierSaveRequest request) {
        return ApiResponse.success("created", medicineService.createSupplier(request));
    }

    @PutMapping("/api/v1/suppliers/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PHARMACY_MANAGER','INVENTORY_MANAGER')")
    public ApiResponse<SupplierVO> updateSupplier(@PathVariable Long id, @Valid @RequestBody SupplierSaveRequest request) {
        return ApiResponse.success("updated", medicineService.updateSupplier(id, request));
    }
}
