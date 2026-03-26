package com.yaojie.modules.inventory.mapper;

import com.yaojie.modules.inventory.entity.InventoryRecord;
import com.yaojie.modules.inventory.vo.InventoryRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InventoryRecordMapper {

    int insert(InventoryRecord inventoryRecord);

    List<InventoryRecordVO> selectList(@Param("keyword") String keyword, @Param("sourceType") String sourceType);
}
