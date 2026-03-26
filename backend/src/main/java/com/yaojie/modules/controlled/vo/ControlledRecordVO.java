package com.yaojie.modules.controlled.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ControlledRecordVO {

    private Long id;
    private String batchNo;
    private String changeType;
    private Integer changeQuantity;
    private Integer beforeQuantity;
    private Integer afterQuantity;
    private String sourceType;
    private Long sourceId;
    private Long operatorId;
    private String operatorName;
    private String remark;
    private LocalDateTime createdAt;
}
