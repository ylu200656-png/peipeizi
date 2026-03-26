package com.yaojie.modules.inventory.mapper;

import com.yaojie.modules.inventory.entity.InventoryCheck;
import com.yaojie.modules.inventory.entity.InventoryCheckItem;
import com.yaojie.modules.inventory.vo.InventoryCheckItemVO;
import com.yaojie.modules.inventory.vo.InventoryCheckVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface InventoryCheckMapper {

    int insert(InventoryCheck inventoryCheck);

    int batchInsertItems(@Param("items") List<InventoryCheckItem> items);

    List<InventoryCheckVO> selectList(@Param("status") String status);

    InventoryCheckVO selectById(@Param("id") Long id);

    List<InventoryCheckItemVO> selectItemsByCheckId(@Param("checkId") Long checkId);

    int updateExecuted(
        @Param("id") Long id,
        @Param("executedBy") Long executedBy,
        @Param("executedAt") LocalDateTime executedAt
    );
}
