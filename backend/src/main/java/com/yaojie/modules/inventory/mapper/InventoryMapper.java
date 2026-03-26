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

    Inventory selectByMedicineIdAndBatchNoForUpdate(@Param("medicineId") Long medicineId, @Param("batchNo") String batchNo);

    int insert(Inventory inventory);

    int insertIgnore(Inventory inventory);

    int updateInbound(Inventory inventory);

    int updateOutbound(Inventory inventory);

    int increaseQuantity(
        @Param("id") Long id,
        @Param("quantity") Integer quantity,
        @Param("productionDate") java.time.LocalDate productionDate,
        @Param("expiryDate") java.time.LocalDate expiryDate,
        @Param("lastInboundTime") java.time.LocalDateTime lastInboundTime
    );

    int decreaseQuantity(
        @Param("id") Long id,
        @Param("quantity") Integer quantity,
        @Param("lastOutboundTime") java.time.LocalDateTime lastOutboundTime
    );

    int setCurrentQuantity(
        @Param("id") Long id,
        @Param("currentQuantity") Integer currentQuantity
    );

    List<InventoryItemVO> selectList(@Param("keyword") String keyword);

    List<InventoryBatchOptionVO> selectAvailableBatches(@Param("medicineId") Long medicineId);

    InventoryBatchOptionVO selectFirstAvailableBatch(@Param("medicineId") Long medicineId);
}
