package com.yaojie.modules.controlled.service.impl;

import com.yaojie.common.enums.ResultCode;
import com.yaojie.common.exception.BusinessException;
import com.yaojie.modules.controlled.mapper.ControlledMedicineMapper;
import com.yaojie.modules.controlled.service.ControlledMedicineService;
import com.yaojie.modules.controlled.vo.ControlledBatchVO;
import com.yaojie.modules.controlled.vo.ControlledMedicineVO;
import com.yaojie.modules.controlled.vo.ControlledOverviewVO;
import com.yaojie.modules.controlled.vo.ControlledRecordVO;
import com.yaojie.modules.warning.vo.WarningRecordVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ControlledMedicineServiceImpl implements ControlledMedicineService {

    private final ControlledMedicineMapper controlledMedicineMapper;

    public ControlledMedicineServiceImpl(ControlledMedicineMapper controlledMedicineMapper) {
        this.controlledMedicineMapper = controlledMedicineMapper;
    }

    @Override
    public ControlledOverviewVO getOverview() {
        return controlledMedicineMapper.selectOverview();
    }

    @Override
    public List<ControlledMedicineVO> listMedicines(String keyword, Integer status) {
        return controlledMedicineMapper.selectMedicineList(keyword, status);
    }

    @Override
    public List<ControlledBatchVO> listBatches(Long medicineId) {
        ensureControlledMedicineExists(medicineId);
        return controlledMedicineMapper.selectBatchesByMedicineId(medicineId);
    }

    @Override
    public List<ControlledRecordVO> listRecords(Long medicineId, Integer limit) {
        ensureControlledMedicineExists(medicineId);
        return controlledMedicineMapper.selectRecordsByMedicineId(medicineId, limit == null ? 20 : limit);
    }

    @Override
    public List<WarningRecordVO> listWarnings(Long medicineId, String status) {
        ensureControlledMedicineExists(medicineId);
        return controlledMedicineMapper.selectWarningsByMedicineId(medicineId, status);
    }

    private void ensureControlledMedicineExists(Long medicineId) {
        ControlledMedicineVO medicine = controlledMedicineMapper.selectMedicineById(medicineId);
        if (medicine == null) {
            throw new BusinessException(ResultCode.MEDICINE_NOT_FOUND);
        }
    }
}
