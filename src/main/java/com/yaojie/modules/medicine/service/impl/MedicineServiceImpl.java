package com.yaojie.modules.medicine.service.impl;

import com.yaojie.common.enums.ResultCode;
import com.yaojie.common.exception.BusinessException;
import com.yaojie.modules.medicine.dto.MedicineCreateRequest;
import com.yaojie.modules.medicine.dto.MedicineQueryRequest;
import com.yaojie.modules.medicine.dto.MedicineUpdateRequest;
import com.yaojie.modules.medicine.entity.Medicine;
import com.yaojie.modules.medicine.mapper.MedicineMapper;
import com.yaojie.modules.medicine.mapper.MedicineSupportMapper;
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

    public MedicineServiceImpl(MedicineMapper medicineMapper, MedicineSupportMapper medicineSupportMapper) {
        this.medicineMapper = medicineMapper;
        this.medicineSupportMapper = medicineSupportMapper;
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
}
