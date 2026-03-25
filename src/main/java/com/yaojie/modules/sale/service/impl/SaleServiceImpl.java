package com.yaojie.modules.sale.service.impl;

import com.yaojie.common.enums.ResultCode;
import com.yaojie.common.exception.BusinessException;
import com.yaojie.modules.audit.entity.OperationLog;
import com.yaojie.modules.audit.mapper.OperationLogMapper;
import com.yaojie.modules.inventory.entity.Inventory;
import com.yaojie.modules.inventory.entity.InventoryRecord;
import com.yaojie.modules.inventory.mapper.InventoryMapper;
import com.yaojie.modules.inventory.mapper.InventoryRecordMapper;
import com.yaojie.modules.medicine.entity.Medicine;
import com.yaojie.modules.medicine.mapper.MedicineMapper;
import com.yaojie.modules.sale.dto.SaleCreateItemRequest;
import com.yaojie.modules.sale.dto.SaleCreateRequest;
import com.yaojie.modules.sale.entity.SaleOrder;
import com.yaojie.modules.sale.entity.SaleOrderItem;
import com.yaojie.modules.sale.mapper.SaleOrderItemMapper;
import com.yaojie.modules.sale.mapper.SaleOrderMapper;
import com.yaojie.modules.sale.service.SaleService;
import com.yaojie.modules.sale.vo.SaleOrderVO;
import com.yaojie.modules.system.entity.SysUser;
import com.yaojie.modules.system.mapper.SysUserMapper;
import com.yaojie.modules.warning.mapper.WarningRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class SaleServiceImpl implements SaleService {

    private static final DateTimeFormatter ORDER_NO_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final SaleOrderMapper saleOrderMapper;
    private final SaleOrderItemMapper saleOrderItemMapper;
    private final InventoryMapper inventoryMapper;
    private final InventoryRecordMapper inventoryRecordMapper;
    private final WarningRecordMapper warningRecordMapper;
    private final OperationLogMapper operationLogMapper;
    private final MedicineMapper medicineMapper;
    private final SysUserMapper sysUserMapper;

    public SaleServiceImpl(
        SaleOrderMapper saleOrderMapper,
        SaleOrderItemMapper saleOrderItemMapper,
        InventoryMapper inventoryMapper,
        InventoryRecordMapper inventoryRecordMapper,
        WarningRecordMapper warningRecordMapper,
        OperationLogMapper operationLogMapper,
        MedicineMapper medicineMapper,
        SysUserMapper sysUserMapper
    ) {
        this.saleOrderMapper = saleOrderMapper;
        this.saleOrderItemMapper = saleOrderItemMapper;
        this.inventoryMapper = inventoryMapper;
        this.inventoryRecordMapper = inventoryRecordMapper;
        this.warningRecordMapper = warningRecordMapper;
        this.operationLogMapper = operationLogMapper;
        this.medicineMapper = medicineMapper;
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SaleOrderVO create(SaleCreateRequest request, String username, String ip) {
        SysUser operator = sysUserMapper.selectByUsername(username);
        if (operator == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        List<String> roleCodes = sysUserMapper.selectRoleCodesByUserId(operator.getId());
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<SaleOrderItem> saleItems = new ArrayList<>();

        for (SaleCreateItemRequest item : request.getItems()) {
            Medicine medicine = medicineMapper.selectEntityById(item.getMedicineId());
            if (medicine == null || medicine.getStatus() == null || medicine.getStatus() != 1) {
                throw new BusinessException(ResultCode.MEDICINE_NOT_FOUND);
            }

            Inventory inventory = inventoryMapper.selectByMedicineIdAndBatchNo(item.getMedicineId(), item.getBatchNo());
            if (inventory == null) {
                throw new BusinessException(ResultCode.INVENTORY_NOT_FOUND);
            }

            if (inventory.getExpiryDate().isBefore(LocalDate.now())) {
                throw new BusinessException(ResultCode.MEDICINE_EXPIRED);
            }

            int availableQuantity = inventory.getCurrentQuantity() - inventory.getLockedQuantity();
            if (availableQuantity < item.getQuantity()) {
                throw new BusinessException(ResultCode.INVENTORY_NOT_ENOUGH);
            }

            if (Integer.valueOf(1).equals(medicine.getIsControlled())
                && !hasAnyRole(roleCodes, "ADMIN", "PHARMACY_MANAGER")) {
                throw new BusinessException(ResultCode.FORBIDDEN, "Current user cannot sell controlled medicine");
            }

            BigDecimal subtotal = medicine.getSalePrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            SaleOrderItem saleOrderItem = new SaleOrderItem();
            saleOrderItem.setMedicineId(item.getMedicineId());
            saleOrderItem.setBatchNo(item.getBatchNo());
            saleOrderItem.setQuantity(item.getQuantity());
            saleOrderItem.setSalePrice(medicine.getSalePrice());
            saleOrderItem.setSubtotal(subtotal);
            saleItems.add(saleOrderItem);
        }

        SaleOrder saleOrder = new SaleOrder();
        saleOrder.setOrderNo(generateOrderNo());
        saleOrder.setOperatorId(operator.getId());
        saleOrder.setTotalAmount(totalAmount);
        saleOrder.setStatus("COMPLETED");
        saleOrder.setRemark(request.getRemark());
        saleOrderMapper.insert(saleOrder);

        for (SaleOrderItem item : saleItems) {
            item.setSaleOrderId(saleOrder.getId());
        }
        saleOrderItemMapper.batchInsert(saleItems);

        for (SaleOrderItem item : saleItems) {
            updateInventoryForSale(item, saleOrder, operator.getId(), request.getRemark());
        }

        refreshWarnings();

        OperationLog operationLog = new OperationLog();
        operationLog.setUserId(operator.getId());
        operationLog.setModuleName("SALE");
        operationLog.setOperationType("CREATE");
        operationLog.setBusinessNo(saleOrder.getOrderNo());
        operationLog.setContent("Sale outbound created");
        operationLog.setIp(ip);
        operationLogMapper.insert(operationLog);

        return getById(saleOrder.getId());
    }

    @Override
    public List<SaleOrderVO> list() {
        List<SaleOrderVO> orders = saleOrderMapper.selectList();
        orders.forEach(order -> order.setItems(saleOrderItemMapper.selectBySaleOrderId(order.getId())));
        return orders;
    }

    @Override
    public SaleOrderVO getById(Long id) {
        SaleOrderVO order = saleOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "Sale order not found");
        }
        order.setItems(saleOrderItemMapper.selectBySaleOrderId(id));
        return order;
    }

    private void updateInventoryForSale(SaleOrderItem item, SaleOrder saleOrder, Long operatorId, String remark) {
        Inventory currentInventory = inventoryMapper.selectByMedicineIdAndBatchNo(item.getMedicineId(), item.getBatchNo());
        if (currentInventory == null) {
            throw new BusinessException(ResultCode.INVENTORY_NOT_FOUND);
        }

        int beforeQuantity = currentInventory.getCurrentQuantity();
        int afterQuantity = beforeQuantity - item.getQuantity();
        if (afterQuantity < 0) {
            throw new BusinessException(ResultCode.INVENTORY_NOT_ENOUGH);
        }

        currentInventory.setCurrentQuantity(afterQuantity);
        currentInventory.setLastOutboundTime(LocalDateTime.now());
        inventoryMapper.updateOutbound(currentInventory);

        InventoryRecord record = new InventoryRecord();
        record.setMedicineId(item.getMedicineId());
        record.setBatchNo(item.getBatchNo());
        record.setChangeType("OUT");
        record.setChangeQuantity(item.getQuantity());
        record.setBeforeQuantity(beforeQuantity);
        record.setAfterQuantity(afterQuantity);
        record.setSourceType("SALE");
        record.setSourceId(saleOrder.getId());
        record.setOperatorId(operatorId);
        record.setRemark(remark);
        inventoryRecordMapper.insert(record);
    }

    private void refreshWarnings() {
        warningRecordMapper.deleteOpenWarnings();
        warningRecordMapper.insertLowStockWarnings();
        warningRecordMapper.insertExpiredWarnings();
        warningRecordMapper.insertExpirySoonWarnings();
    }

    private boolean hasAnyRole(List<String> roleCodes, String... targets) {
        for (String target : targets) {
            if (roleCodes.contains(target)) {
                return true;
            }
        }
        return false;
    }

    private String generateOrderNo() {
        return "SO-" + LocalDateTime.now().format(ORDER_NO_FORMATTER);
    }
}
