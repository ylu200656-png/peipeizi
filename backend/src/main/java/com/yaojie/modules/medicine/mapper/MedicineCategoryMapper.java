package com.yaojie.modules.medicine.mapper;

import com.yaojie.modules.medicine.entity.MedicineCategory;
import com.yaojie.modules.medicine.vo.MedicineCategoryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MedicineCategoryMapper {

    MedicineCategory selectById(@Param("id") Long id);

    MedicineCategory selectByCategoryCode(@Param("categoryCode") String categoryCode);

    List<MedicineCategoryVO> selectManageList();

    int insert(MedicineCategory medicineCategory);

    int updateById(MedicineCategory medicineCategory);
}
