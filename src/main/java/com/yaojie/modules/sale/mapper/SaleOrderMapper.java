package com.yaojie.modules.sale.mapper;

import com.yaojie.modules.sale.entity.SaleOrder;
import com.yaojie.modules.sale.vo.SaleOrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SaleOrderMapper {

    int insert(SaleOrder saleOrder);

    List<SaleOrderVO> selectList();

    SaleOrderVO selectById(@Param("id") Long id);
}
