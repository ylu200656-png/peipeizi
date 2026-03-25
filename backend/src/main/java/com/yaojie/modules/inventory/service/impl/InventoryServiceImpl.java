package com.yaojie.modules.inventory.service.impl;

import com.yaojie.modules.inventory.mapper.InventoryMapper;
import com.yaojie.modules.inventory.service.InventoryService;
import com.yaojie.modules.inventory.vo.InventoryBatchOptionVO;
import com.yaojie.modules.inventory.vo.InventoryItemVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryMapper inventoryMapper;

    public InventoryServiceImpl(InventoryMapper inventoryMapper) {
        this.inventoryMapper = inventoryMapper;
    }

    @Override
    public List<InventoryItemVO> list(String keyword) {
        return inventoryMapper.selectList(keyword);
    }

    @Override
    public List<InventoryBatchOptionVO> listAvailableBatches(Long medicineId) {
        return inventoryMapper.selectAvailableBatches(medicineId);
    }
}
