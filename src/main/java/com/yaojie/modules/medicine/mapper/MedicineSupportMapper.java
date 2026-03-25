package com.yaojie.modules.medicine.mapper;

import com.yaojie.modules.medicine.vo.MedicineCategoryVO;
import com.yaojie.modules.medicine.vo.SupplierVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MedicineSupportMapper {

    List<MedicineCategoryVO> selectCategoryList();

    List<SupplierVO> selectSupplierList();
}
