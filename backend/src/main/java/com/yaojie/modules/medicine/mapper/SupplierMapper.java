package com.yaojie.modules.medicine.mapper;

import com.yaojie.modules.medicine.entity.Supplier;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SupplierMapper {

    Supplier selectById(@Param("id") Long id);
}
