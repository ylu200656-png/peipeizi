package com.yaojie.modules.audit.service;

import com.yaojie.modules.audit.vo.OperationLogVO;

import java.util.List;

public interface AuditService {

    List<OperationLogVO> listOperationLogs(String moduleName, String operationType);
}
