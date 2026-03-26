package com.yaojie.modules.medicine.service;

import com.yaojie.modules.medicine.dto.MedicineCategorySaveRequest;
import com.yaojie.modules.medicine.dto.MedicineCreateRequest;
import com.yaojie.modules.medicine.dto.MedicineQueryRequest;
import com.yaojie.modules.medicine.dto.MedicineUpdateRequest;
import com.yaojie.modules.medicine.dto.SupplierSaveRequest;
import com.yaojie.modules.medicine.vo.MedicineCategoryVO;
import com.yaojie.modules.medicine.vo.MedicineVO;
import com.yaojie.modules.medicine.vo.SupplierVO;

import java.util.List;

public interface MedicineService {

    MedicineVO create(MedicineCreateRequest request);

    MedicineVO update(Long id, MedicineUpdateRequest request);

    MedicineVO getById(Long id);

    List<MedicineVO> list(MedicineQueryRequest queryRequest);

    List<MedicineCategoryVO> categoryList();

    List<SupplierVO> supplierList();

    List<MedicineCategoryVO> categoryManageList();

    MedicineCategoryVO createCategory(MedicineCategorySaveRequest request);

    MedicineCategoryVO updateCategory(Long id, MedicineCategorySaveRequest request);

    List<SupplierVO> supplierManageList();

    SupplierVO createSupplier(SupplierSaveRequest request);

    SupplierVO updateSupplier(Long id, SupplierSaveRequest request);
}
