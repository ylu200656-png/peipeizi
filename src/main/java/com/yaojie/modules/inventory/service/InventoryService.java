package com.yaojie.modules.inventory.service;

import com.yaojie.modules.inventory.vo.InventoryBatchOptionVO;
import com.yaojie.modules.inventory.vo.InventoryItemVO;

import java.util.List;

public interface InventoryService {

    List<InventoryItemVO> list(String keyword);

    List<InventoryBatchOptionVO> listAvailableBatches(Long medicineId);
}
