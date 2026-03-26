package com.yaojie.modules.inventory.service;

import com.yaojie.modules.inventory.dto.InventoryCheckCreateRequest;
import com.yaojie.modules.inventory.vo.InventoryCheckVO;
import com.yaojie.modules.inventory.vo.InventoryBatchOptionVO;
import com.yaojie.modules.inventory.vo.InventoryItemVO;
import com.yaojie.modules.inventory.vo.InventoryRecordVO;

import java.util.List;

public interface InventoryService {

    List<InventoryItemVO> list(String keyword);

    List<InventoryBatchOptionVO> listAvailableBatches(Long medicineId);

    List<InventoryRecordVO> listRecords(String keyword, String sourceType);

    List<InventoryCheckVO> listChecks(String status);

    InventoryCheckVO getCheck(Long id);

    InventoryCheckVO createCheck(InventoryCheckCreateRequest request, String username, String ip);

    InventoryCheckVO executeCheck(Long id, String username, String ip);
}
