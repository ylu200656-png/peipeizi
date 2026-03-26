package com.yaojie.modules.medicine.service.impl;

import com.yaojie.common.enums.ResultCode;
import com.yaojie.common.exception.BusinessException;
import com.yaojie.modules.medicine.dto.MedicineCategorySaveRequest;
import com.yaojie.modules.medicine.dto.MedicineCreateRequest;
import com.yaojie.modules.medicine.dto.MedicineQueryRequest;
import com.yaojie.modules.medicine.dto.MedicineUpdateRequest;
import com.yaojie.modules.medicine.dto.SupplierSaveRequest;
import com.yaojie.modules.medicine.entity.MedicineCategory;
import com.yaojie.modules.medicine.entity.Medicine;
import com.yaojie.modules.medicine.entity.Supplier;
import com.yaojie.modules.medicine.mapper.MedicineCategoryMapper;
import com.yaojie.modules.medicine.mapper.MedicineMapper;
import com.yaojie.modules.medicine.mapper.MedicineSupportMapper;
import com.yaojie.modules.medicine.mapper.SupplierMapper;
import com.yaojie.modules.medicine.service.MedicineService;
import com.yaojie.modules.medicine.vo.MedicineCategoryVO;
import com.yaojie.modules.medicine.vo.MedicineVO;
import com.yaojie.modules.medicine.vo.SupplierVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicineServiceImpl implements MedicineService {

    private final MedicineMapper medicineMapper;
    private final MedicineSupportMapper medicineSupportMapper;
    private final MedicineCategoryMapper medicineCategoryMapper;
    private final SupplierMapper supplierMapper;

    public MedicineServiceImpl(
        MedicineMapper medicineMapper,
        MedicineSupportMapper medicineSupportMapper,
        MedicineCategoryMapper medicineCategoryMapper,
        SupplierMapper supplierMapper
    ) {
        this.medicineMapper = medicineMapper;
        this.medicineSupportMapper = medicineSupportMapper;
        this.medicineCategoryMapper = medicineCategoryMapper;
        this.supplierMapper = supplierMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MedicineVO create(MedicineCreateRequest request) {
        Medicine existing = medicineMapper.selectByMedicineCode(request.getMedicineCode());
        if (existing != null) {
            throw new BusinessException(ResultCode.MEDICINE_CODE_EXISTS);
        }

        Medicine medicine = new Medicine();
        BeanUtils.copyProperties(request, medicine);
        medicineMapper.insert(medicine);
        return getById(medicine.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MedicineVO update(Long id, MedicineUpdateRequest request) {
        Medicine medicine = medicineMapper.selectEntityById(id);
        if (medicine == null) {
            throw new BusinessException(ResultCode.MEDICINE_NOT_FOUND);
        }

        BeanUtils.copyProperties(request, medicine);
        medicine.setId(id);
        medicineMapper.updateById(medicine);
        return getById(id);
    }

    @Override
    public MedicineVO getById(Long id) {
        MedicineVO medicine = medicineMapper.selectById(id);
        if (medicine == null) {
            throw new BusinessException(ResultCode.MEDICINE_NOT_FOUND);
        }
        return medicine;
    }

    @Override
    public List<MedicineVO> list(MedicineQueryRequest queryRequest) {
        return medicineMapper.selectList(queryRequest);
    }

    @Override
    public List<MedicineCategoryVO> categoryList() {
        return medicineSupportMapper.selectCategoryList();
    }

    @Override
    public List<SupplierVO> supplierList() {
        return medicineSupportMapper.selectSupplierList();
    }

    @Override
    public List<MedicineCategoryVO> categoryManageList() {
        return medicineCategoryMapper.selectManageList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MedicineCategoryVO createCategory(MedicineCategorySaveRequest request) {
        String categoryCode = trimToNull(request.getCategoryCode());
        if (categoryCode == null || trimToNull(request.getCategoryName()) == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "Category name and code are required");
        }
        if (medicineCategoryMapper.selectByCategoryCode(categoryCode) != null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "Category code already exists");
        }

        MedicineCategory category = new MedicineCategory();
        category.setCategoryName(request.getCategoryName().trim());
        category.setCategoryCode(categoryCode);
        category.setRemark(trimToNull(request.getRemark()));
        medicineCategoryMapper.insert(category);
        return requireCategory(category.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MedicineCategoryVO updateCategory(Long id, MedicineCategorySaveRequest request) {
        MedicineCategory category = medicineCategoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "Category not found");
        }

        String categoryCode = trimToNull(request.getCategoryCode());
        String categoryName = trimToNull(request.getCategoryName());
        if (categoryCode == null || categoryName == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "Category name and code are required");
        }
        MedicineCategory codeOwner = medicineCategoryMapper.selectByCategoryCode(categoryCode);
        if (codeOwner != null && !codeOwner.getId().equals(id)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "Category code already exists");
        }

        category.setCategoryName(categoryName);
        category.setCategoryCode(categoryCode);
        category.setRemark(trimToNull(request.getRemark()));
        medicineCategoryMapper.updateById(category);
        return requireCategory(id);
    }

    @Override
    public List<SupplierVO> supplierManageList() {
        return supplierMapper.selectManageList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SupplierVO createSupplier(SupplierSaveRequest request) {
        String supplierName = trimToNull(request.getSupplierName());
        if (supplierName == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "Supplier name is required");
        }
        if (supplierMapper.selectBySupplierName(supplierName) != null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "Supplier name already exists");
        }

        Supplier supplier = new Supplier();
        supplier.setSupplierName(supplierName);
        supplier.setContactPerson(trimToNull(request.getContactPerson()));
        supplier.setPhone(trimToNull(request.getPhone()));
        supplier.setAddress(trimToNull(request.getAddress()));
        supplier.setStatus(normalizeStatus(request.getStatus()));
        supplierMapper.insert(supplier);
        return requireSupplier(supplier.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SupplierVO updateSupplier(Long id, SupplierSaveRequest request) {
        Supplier supplier = supplierMapper.selectById(id);
        if (supplier == null) {
            throw new BusinessException(ResultCode.SUPPLIER_NOT_FOUND);
        }
        String supplierName = trimToNull(request.getSupplierName());
        if (supplierName == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "Supplier name is required");
        }
        Supplier nameOwner = supplierMapper.selectBySupplierName(supplierName);
        if (nameOwner != null && !nameOwner.getId().equals(id)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "Supplier name already exists");
        }

        supplier.setSupplierName(supplierName);
        supplier.setContactPerson(trimToNull(request.getContactPerson()));
        supplier.setPhone(trimToNull(request.getPhone()));
        supplier.setAddress(trimToNull(request.getAddress()));
        supplier.setStatus(normalizeStatus(request.getStatus()));
        supplierMapper.updateById(supplier);
        return requireSupplier(id);
    }

    private MedicineCategoryVO requireCategory(Long id) {
        return categoryManageList().stream()
            .filter(item -> item.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "Category not found"));
    }

    private SupplierVO requireSupplier(Long id) {
        return supplierManageList().stream()
            .filter(item -> item.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new BusinessException(ResultCode.SUPPLIER_NOT_FOUND));
    }

    private Integer normalizeStatus(Integer status) {
        if (status == null) {
            return 1;
        }
        if (status != 0 && status != 1) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "Status is invalid");
        }
        return status;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
