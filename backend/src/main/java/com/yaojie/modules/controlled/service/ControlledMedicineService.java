package com.yaojie.modules.controlled.service;

import com.yaojie.modules.controlled.vo.ControlledBatchVO;
import com.yaojie.modules.controlled.vo.ControlledMedicineVO;
import com.yaojie.modules.controlled.vo.ControlledOverviewVO;
import com.yaojie.modules.controlled.vo.ControlledRecordVO;
import com.yaojie.modules.warning.vo.WarningRecordVO;

import java.util.List;

public interface ControlledMedicineService {

    ControlledOverviewVO getOverview();

    List<ControlledMedicineVO> listMedicines(String keyword, Integer status);

    List<ControlledBatchVO> listBatches(Long medicineId);

    List<ControlledRecordVO> listRecords(Long medicineId, Integer limit);

    List<WarningRecordVO> listWarnings(Long medicineId, String status);
}
