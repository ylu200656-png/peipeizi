package com.yaojie.modules.purchase.service.impl;

import com.yaojie.common.enums.ResultCode;
import com.yaojie.common.exception.BusinessException;
import com.yaojie.modules.audit.entity.OperationLog;
import com.yaojie.modules.audit.mapper.OperationLogMapper;
import com.yaojie.modules.inventory.entity.Inventory;
import com.yaojie.modules.inventory.entity.InventoryRecord;
import com.yaojie.modules.inventory.mapper.InventoryMapper;
import com.yaojie.modules.inventory.mapper.InventoryRecordMapper;
import com.yaojie.modules.medicine.entity.Medicine;
import com.yaojie.modules.medicine.entity.Supplier;
import com.yaojie.modules.medicine.mapper.MedicineMapper;
import com.yaojie.modules.medicine.mapper.SupplierMapper;
import com.yaojie.modules.purchase.dto.PurchaseCreateItemRequest;
import com.yaojie.modules.purchase.dto.PurchaseCreateRequest;
import com.yaojie.modules.purchase.entity.PurchaseOrder;
import com.yaojie.modules.purchase.entity.PurchaseOrderItem;
import com.yaojie.modules.purchase.mapper.PurchaseOrderItemMapper;
import com.yaojie.modules.purchase.mapper.PurchaseOrderMapper;
import com.yaojie.modules.purchase.service.PurchaseService;
import com.yaojie.modules.purchase.vo.PurchaseOrderVO;
import com.yaojie.modules.system.entity.SysUser;
import com.yaojie.modules.system.mapper.SysUserMapper;
import com.yaojie.modules.warning.mapper.WarningRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    private static final DateTimeFormatter ORDER_NO_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final PurchaseOrderMapper purchaseOrderMapper;
    private final PurchaseOrderItemMapper purchaseOrderItemMapper;
    private final InventoryMapper inventoryMapper;
    private final InventoryRecordMapper inventoryRecordMapper;
    private final WarningRecordMapper warningRecordMapper;
    private final OperationLogMapper operationLogMapper;
    private final MedicineMapper medicineMapper;
    private final SupplierMapper supplierMapper;
    private final SysUserMapper sysUserMapper;

    public PurchaseServiceImpl(
        PurchaseOrderMapper purchaseOrderMapper,
        PurchaseOrderItemMapper purchaseOrderItemMapper,
        InventoryMapper inventoryMapper,
        InventoryRecordMapper inventoryRecordMapper,
        WarningRecordMapper warningRecordMapper,
        OperationLogMapper operationLogMapper,
        MedicineMapper medicineMapper,
        SupplierMapper supplierMapper,
        SysUserMapper sysUserMapper
    ) {
        this.purchaseOrderMapper = purchaseOrderMapper;
        this.purchaseOrderItemMapper = purchaseOrderItemMapper;
        this.inventoryMapper = inventoryMapper;
        this.inventoryRecordMapper = inventoryRecordMapper;
        this.warningRecordMapper = warningRecordMapper;
        this.operationLogMapper = operationLogMapper;
        this.medicineMapper = medicineMapper;
        this.supplierMapper = supplierMapper;
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PurchaseOrderVO create(PurchaseCreateRequest request, String username, String ip) {
        SysUser operator = sysUserMapper.selectByUsername(username);
        if (operator == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        Supplier supplier = supplierMapper.selectById(request.getSupplierId());
        if (supplier == null || supplier.getStatus() == null || supplier.getStatus() != 1) {
            throw new BusinessException(ResultCode.SUPPLIER_NOT_FOUND);
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<PurchaseOrderItem> orderItems = new ArrayList<>();

        for (PurchaseCreateItemRequest item : request.getItems()) {
            Medicine medicine = medicineMapper.selectEntityById(item.getMedicineId());
            if (medicine == null) {
                throw new BusinessException(ResultCode.MEDICINE_NOT_FOUND);
            }
            if (!item.getExpiryDate().isAfter(item.getProductionDate())) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "Expiry date must be later than production date");
            }

            BigDecimal subtotal = item.getPurchasePrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            PurchaseOrderItem orderItem = new PurchaseOrderItem();
            orderItem.setMedicineId(item.getMedicineId());
            orderItem.setBatchNo(item.getBatchNo());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPurchasePrice(item.getPurchasePrice());
            orderItem.setProductionDate(item.getProductionDate());
            orderItem.setExpiryDate(item.getExpiryDate());
            orderItem.setSubtotal(subtotal);
            orderItems.add(orderItem);
        }

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setOrderNo(generateOrderNo());
        purchaseOrder.setSupplierId(request.getSupplierId());
        purchaseOrder.setOperatorId(operator.getId());
        purchaseOrder.setTotalAmount(totalAmount);
        purchaseOrder.setStatus("COMPLETED");
        purchaseOrder.setRemark(request.getRemark());
        purchaseOrderMapper.insert(purchaseOrder);

        for (PurchaseOrderItem item : orderItems) {
            item.setPurchaseOrderId(purchaseOrder.getId());
        }
        purchaseOrderItemMapper.batchInsert(orderItems);

        for (PurchaseOrderItem item : orderItems) {
            upsertInventory(item, purchaseOrder, operator.getId(), request.getRemark());
        }

        refreshWarnings();

        OperationLog operationLog = new OperationLog();
        operationLog.setUserId(operator.getId());
        operationLog.setModuleName("PURCHASE");
        operationLog.setOperationType("CREATE");
        operationLog.setBusinessNo(purchaseOrder.getOrderNo());
        operationLog.setContent("Purchase inbound created");
        operationLog.setIp(ip);
        operationLogMapper.insert(operationLog);

        return getById(purchaseOrder.getId());
    }

    @Override
    public List<PurchaseOrderVO> list() {
        List<PurchaseOrderVO> orders = purchaseOrderMapper.selectList();
        orders.forEach(order -> order.setItems(purchaseOrderItemMapper.selectByPurchaseOrderId(order.getId())));
        return orders;
    }

    @Override
    public PurchaseOrderVO getById(Long id) {
        PurchaseOrderVO order = purchaseOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "Purchase order not found");
        }
        order.setItems(purchaseOrderItemMapper.selectByPurchaseOrderId(id));
        return order;
    }

    private void upsertInventory(PurchaseOrderItem item, PurchaseOrder purchaseOrder, Long operatorId, String remark) {
        LocalDateTime now = LocalDateTime.now();

        Inventory seedInventory = new Inventory();
        seedInventory.setMedicineId(item.getMedicineId());
        seedInventory.setBatchNo(item.getBatchNo());
        seedInventory.setCurrentQuantity(0);
        seedInventory.setLockedQuantity(0);
        seedInventory.setProductionDate(item.getProductionDate());
        seedInventory.setExpiryDate(item.getExpiryDate());
        seedInventory.setLastInboundTime(now);
        inventoryMapper.insertIgnore(seedInventory);

        Inventory currentInventory = inventoryMapper.selectByMedicineIdAndBatchNoForUpdate(item.getMedicineId(), item.getBatchNo());
        if (currentInventory == null) {
            throw new BusinessException(ResultCode.INVENTORY_NOT_FOUND);
        }

        int beforeQuantity = currentInventory.getCurrentQuantity();
        int afterQuantity = beforeQuantity + item.getQuantity();

        inventoryMapper.increaseQuantity(
            currentInventory.getId(),
            item.getQuantity(),
            item.getProductionDate(),
            item.getExpiryDate(),
            now
        );

        InventoryRecord record = new InventoryRecord();
        record.setMedicineId(item.getMedicineId());
        record.setBatchNo(item.getBatchNo());
        record.setChangeType("IN");
        record.setChangeQuantity(item.getQuantity());
        record.setBeforeQuantity(beforeQuantity);
        record.setAfterQuantity(afterQuantity);
        record.setSourceType("PURCHASE");
        record.setSourceId(purchaseOrder.getId());
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

    private String generateOrderNo() {
        return "PO-" + LocalDateTime.now().format(ORDER_NO_FORMATTER);
    }
}
