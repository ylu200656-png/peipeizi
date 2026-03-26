package com.yaojie.modules.inventory.vo;

import lombok.Data;

@Data
public class InventoryCheckItemVO {

    private Long id;
    private Long checkId;
    private Long medicineId;
    private String medicineCode;
    private String medicineName;
    private String batchNo;
    private Integer systemQuantity;
    private Integer actualQuantity;
    private Integer differenceQuantity;
    private String reason;
}
