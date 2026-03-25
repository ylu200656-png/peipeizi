package com.yaojie.modules.inventory.mapper;

import com.yaojie.modules.inventory.entity.Inventory;
import com.yaojie.modules.inventory.vo.InventoryBatchOptionVO;
import com.yaojie.modules.inventory.vo.InventoryItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InventoryMapper {

    Inventory selectByMedicineIdAndBatchNo(@Param("medicineId") Long medicineId, @Param("batchNo") String batchNo);

    int insert(Inventory inventory);

    int updateInbound(Inventory inventory);

    int updateOutbound(Inventory inventory);

    List<InventoryItemVO> selectList(@Param("keyword") String keyword);

    List<InventoryBatchOptionVO> selectAvailableBatches(@Param("medicineId") Long medicineId);
}
