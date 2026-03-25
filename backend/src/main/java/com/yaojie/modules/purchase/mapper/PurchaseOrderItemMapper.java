package com.yaojie.modules.purchase.mapper;

import com.yaojie.modules.purchase.entity.PurchaseOrderItem;
import com.yaojie.modules.purchase.vo.PurchaseOrderItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PurchaseOrderItemMapper {

    int batchInsert(@Param("items") List<PurchaseOrderItem> items);

    List<PurchaseOrderItemVO> selectByPurchaseOrderId(@Param("purchaseOrderId") Long purchaseOrderId);
}
