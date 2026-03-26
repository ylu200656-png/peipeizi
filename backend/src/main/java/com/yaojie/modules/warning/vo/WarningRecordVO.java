package com.yaojie.modules.warning.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WarningRecordVO {

    private Long id;
    private Long medicineId;
    private String medicineCode;
    private String medicineName;
    private String batchNo;
    private String warningType;
    private String warningLevel;
    private String warningMessage;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime handledAt;
    private Long handledBy;
    private String handlerName;
    private String handleRemark;
}
