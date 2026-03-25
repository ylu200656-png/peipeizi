package com.yaojie.modules.inventory.mapper;

import com.yaojie.modules.inventory.entity.InventoryRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InventoryRecordMapper {

    int insert(InventoryRecord inventoryRecord);
}
