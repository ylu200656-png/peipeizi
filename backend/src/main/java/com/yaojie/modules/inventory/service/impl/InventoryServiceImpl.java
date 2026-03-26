package com.yaojie.modules.inventory.service.impl;

import com.yaojie.common.enums.ResultCode;
import com.yaojie.common.exception.BusinessException;
import com.yaojie.modules.audit.entity.OperationLog;
import com.yaojie.modules.audit.mapper.OperationLogMapper;
import com.yaojie.modules.inventory.dto.InventoryCheckCreateItemRequest;
import com.yaojie.modules.inventory.dto.InventoryCheckCreateRequest;
import com.yaojie.modules.inventory.entity.Inventory;
import com.yaojie.modules.inventory.entity.InventoryCheck;
import com.yaojie.modules.inventory.entity.InventoryCheckItem;
import com.yaojie.modules.inventory.mapper.InventoryMapper;
import com.yaojie.modules.inventory.mapper.InventoryCheckMapper;
import com.yaojie.modules.inventory.mapper.InventoryRecordMapper;
import com.yaojie.modules.inventory.service.InventoryService;
import com.yaojie.modules.inventory.vo.InventoryBatchOptionVO;
import com.yaojie.modules.inventory.vo.InventoryCheckVO;
import com.yaojie.modules.inventory.vo.InventoryItemVO;
import com.yaojie.modules.inventory.vo.InventoryRecordVO;
import com.yaojie.modules.system.entity.SysUser;
import com.yaojie.modules.system.mapper.SysUserMapper;
import com.yaojie.modules.warning.mapper.WarningRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    private static final DateTimeFormatter CHECK_NO_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final InventoryMapper inventoryMapper;
    private final InventoryRecordMapper inventoryRecordMapper;
    private final InventoryCheckMapper inventoryCheckMapper;
    private final SysUserMapper sysUserMapper;
    private final OperationLogMapper operationLogMapper;
    private final WarningRecordMapper warningRecordMapper;

    public InventoryServiceImpl(
        InventoryMapper inventoryMapper,
        InventoryRecordMapper inventoryRecordMapper,
        InventoryCheckMapper inventoryCheckMapper,
        SysUserMapper sysUserMapper,
        OperationLogMapper operationLogMapper,
        WarningRecordMapper warningRecordMapper
    ) {
        this.inventoryMapper = inventoryMapper;
        this.inventoryRecordMapper = inventoryRecordMapper;
        this.inventoryCheckMapper = inventoryCheckMapper;
        this.sysUserMapper = sysUserMapper;
        this.operationLogMapper = operationLogMapper;
        this.warningRecordMapper = warningRecordMapper;
    }

    @Override
    public List<InventoryItemVO> list(String keyword) {
        return inventoryMapper.selectList(keyword);
    }

    @Override
    public List<InventoryBatchOptionVO> listAvailableBatches(Long medicineId) {
        return inventoryMapper.selectAvailableBatches(medicineId);
    }

    @Override
    public List<InventoryRecordVO> listRecords(String keyword, String sourceType) {
        return inventoryRecordMapper.selectList(trimToNull(keyword), trimToNull(sourceType));
    }

    @Override
    public List<InventoryCheckVO> listChecks(String status) {
        List<InventoryCheckVO> checks = inventoryCheckMapper.selectList(trimToNull(status));
        checks.forEach(check -> check.setItems(inventoryCheckMapper.selectItemsByCheckId(check.getId())));
        return checks;
    }

    @Override
    public InventoryCheckVO getCheck(Long id) {
        InventoryCheckVO check = inventoryCheckMapper.selectById(id);
        if (check == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "Inventory check not found");
        }
        check.setItems(inventoryCheckMapper.selectItemsByCheckId(id));
        return check;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InventoryCheckVO createCheck(InventoryCheckCreateRequest request, String username, String ip) {
        SysUser operator = requireOperator(username);

        InventoryCheck check = new InventoryCheck();
        check.setCheckNo(generateCheckNo());
        check.setStatus("DRAFT");
        check.setRemark(trimToNull(request.getRemark()));
        check.setCreatedBy(operator.getId());
        inventoryCheckMapper.insert(check);

        List<InventoryCheckItem> items = new ArrayList<>();
        for (InventoryCheckCreateItemRequest requestItem : request.getItems()) {
            Inventory inventory = inventoryMapper.selectByMedicineIdAndBatchNo(requestItem.getMedicineId(), requestItem.getBatchNo().trim());
            if (inventory == null) {
                throw new BusinessException(ResultCode.INVENTORY_NOT_FOUND);
            }

            InventoryCheckItem item = new InventoryCheckItem();
            item.setCheckId(check.getId());
            item.setMedicineId(requestItem.getMedicineId());
            item.setBatchNo(requestItem.getBatchNo().trim());
            item.setSystemQuantity(inventory.getCurrentQuantity());
            item.setActualQuantity(requestItem.getActualQuantity());
            item.setDifferenceQuantity(requestItem.getActualQuantity() - inventory.getCurrentQuantity());
            item.setReason(trimToNull(requestItem.getReason()));
            items.add(item);
        }
        inventoryCheckMapper.batchInsertItems(items);

        insertLog(operator.getId(), "INVENTORY", "CHECK_CREATE", check.getCheckNo(), "Create inventory check", ip);
        return getCheck(check.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InventoryCheckVO executeCheck(Long id, String username, String ip) {
        InventoryCheckVO check = getCheck(id);
        if (!"DRAFT".equals(check.getStatus())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "Inventory check has already been executed");
        }

        SysUser operator = requireOperator(username);
        boolean changed = false;
        for (var item : check.getItems()) {
            Inventory inventory = inventoryMapper.selectByMedicineIdAndBatchNoForUpdate(item.getMedicineId(), item.getBatchNo());
            if (inventory == null) {
                throw new BusinessException(ResultCode.INVENTORY_NOT_FOUND);
            }
            if (!inventory.getCurrentQuantity().equals(item.getSystemQuantity())) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "Inventory changed after check creation, please recreate the check");
            }
            if (item.getDifferenceQuantity() == null || item.getDifferenceQuantity() == 0) {
                continue;
            }

            inventoryMapper.setCurrentQuantity(inventory.getId(), item.getActualQuantity());

            com.yaojie.modules.inventory.entity.InventoryRecord record = new com.yaojie.modules.inventory.entity.InventoryRecord();
            record.setMedicineId(item.getMedicineId());
            record.setBatchNo(item.getBatchNo());
            record.setChangeType("ADJUST");
            record.setChangeQuantity(item.getDifferenceQuantity());
            record.setBeforeQuantity(item.getSystemQuantity());
            record.setAfterQuantity(item.getActualQuantity());
            record.setSourceType("CHECK");
            record.setSourceId(check.getId());
            record.setOperatorId(operator.getId());
            record.setRemark(item.getReason() == null ? check.getRemark() : item.getReason());
            inventoryRecordMapper.insert(record);
            changed = true;
        }

        inventoryCheckMapper.updateExecuted(id, operator.getId(), LocalDateTime.now());
        if (changed) {
            refreshWarnings();
        }
        insertLog(operator.getId(), "INVENTORY", "CHECK_EXECUTE", check.getCheckNo(), "Execute inventory check", ip);
        return getCheck(id);
    }

    private SysUser requireOperator(String username) {
        SysUser operator = sysUserMapper.selectByUsername(username);
        if (operator == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return operator;
    }

    private void refreshWarnings() {
        warningRecordMapper.deleteOpenWarnings();
        warningRecordMapper.insertLowStockWarnings();
        warningRecordMapper.insertExpiredWarnings();
        warningRecordMapper.insertExpirySoonWarnings();
    }

    private void insertLog(Long userId, String moduleName, String operationType, String businessNo, String content, String ip) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setModuleName(moduleName);
        log.setOperationType(operationType);
        log.setBusinessNo(businessNo);
        log.setContent(content);
        log.setIp(ip);
        operationLogMapper.insert(log);
    }

    private String generateCheckNo() {
        return "CHK-" + LocalDateTime.now().format(CHECK_NO_FORMATTER);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
