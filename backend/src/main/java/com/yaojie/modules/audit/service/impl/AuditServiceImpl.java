package com.yaojie.modules.audit.service.impl;

import com.yaojie.modules.audit.mapper.OperationLogMapper;
import com.yaojie.modules.audit.service.AuditService;
import com.yaojie.modules.audit.vo.OperationLogVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditServiceImpl implements AuditService {

    private final OperationLogMapper operationLogMapper;

    public AuditServiceImpl(OperationLogMapper operationLogMapper) {
        this.operationLogMapper = operationLogMapper;
    }

    @Override
    public List<OperationLogVO> listOperationLogs(String moduleName, String operationType) {
        return operationLogMapper.selectList(moduleName, operationType);
    }
}
