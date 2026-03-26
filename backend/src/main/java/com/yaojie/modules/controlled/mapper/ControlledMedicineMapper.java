package com.yaojie.modules.controlled.mapper;

import com.yaojie.modules.controlled.vo.ControlledBatchVO;
import com.yaojie.modules.controlled.vo.ControlledMedicineVO;
import com.yaojie.modules.controlled.vo.ControlledOverviewVO;
import com.yaojie.modules.controlled.vo.ControlledRecordVO;
import com.yaojie.modules.warning.vo.WarningRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ControlledMedicineMapper {

    ControlledOverviewVO selectOverview();

    List<ControlledMedicineVO> selectMedicineList(@Param("keyword") String keyword, @Param("status") Integer status);

    ControlledMedicineVO selectMedicineById(@Param("id") Long id);

    List<ControlledBatchVO> selectBatchesByMedicineId(@Param("medicineId") Long medicineId);

    List<ControlledRecordVO> selectRecordsByMedicineId(@Param("medicineId") Long medicineId, @Param("limit") Integer limit);

    List<WarningRecordVO> selectWarningsByMedicineId(@Param("medicineId") Long medicineId, @Param("status") String status);
}
