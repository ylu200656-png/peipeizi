package com.yaojie.modules.medicine.mapper;

import com.yaojie.modules.medicine.dto.MedicineQueryRequest;
import com.yaojie.modules.medicine.entity.Medicine;
import com.yaojie.modules.medicine.vo.MedicineVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MedicineMapper {

    MedicineVO selectById(@Param("id") Long id);

    Medicine selectEntityById(@Param("id") Long id);

    Medicine selectByMedicineCode(@Param("medicineCode") String medicineCode);

    List<MedicineVO> selectList(@Param("query") MedicineQueryRequest query);

    int insert(Medicine medicine);

    int updateById(Medicine medicine);
}
