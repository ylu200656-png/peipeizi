package com.yaojie.modules.purchase.mapper;

import com.yaojie.modules.purchase.entity.PurchaseOrder;
import com.yaojie.modules.purchase.vo.PurchaseOrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PurchaseOrderMapper {

    int insert(PurchaseOrder purchaseOrder);

    List<PurchaseOrderVO> selectList();

    PurchaseOrderVO selectById(@Param("id") Long id);
}
