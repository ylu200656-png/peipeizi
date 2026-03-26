package com.yaojie.modules.inventory.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class InventoryCheckVO {

    private Long id;
    private String checkNo;
    private String status;
    private String remark;
    private Long createdBy;
    private String createdByName;
    private Long executedBy;
    private String executedByName;
    private LocalDateTime createdAt;
    private LocalDateTime executedAt;
    private List<InventoryCheckItemVO> items;
}
