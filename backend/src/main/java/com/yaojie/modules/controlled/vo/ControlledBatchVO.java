package com.yaojie.modules.controlled.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ControlledBatchVO {

    private String batchNo;
    private Integer currentQuantity;
    private Integer lockedQuantity;
    private Integer availableQuantity;
    private LocalDate productionDate;
    private LocalDate expiryDate;
    private LocalDateTime lastInboundTime;
    private LocalDateTime lastOutboundTime;
    private Boolean expired;
    private Boolean expirySoon;
}
