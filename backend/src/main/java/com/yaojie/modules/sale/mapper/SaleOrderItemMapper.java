package com.yaojie.modules.sale.mapper;

import com.yaojie.modules.sale.entity.SaleOrderItem;
import com.yaojie.modules.sale.vo.SaleOrderItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SaleOrderItemMapper {

    int batchInsert(@Param("items") List<SaleOrderItem> items);

    List<SaleOrderItemVO> selectBySaleOrderId(@Param("saleOrderId") Long saleOrderId);
}
