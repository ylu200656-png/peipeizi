package com.yaojie.modules.medicine.mapper;

import com.yaojie.modules.medicine.entity.Supplier;
import com.yaojie.modules.medicine.vo.SupplierVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SupplierMapper {

    Supplier selectById(@Param("id") Long id);

    Supplier selectBySupplierName(@Param("supplierName") String supplierName);

    List<SupplierVO> selectManageList();

    int insert(Supplier supplier);

    int updateById(Supplier supplier);
}
