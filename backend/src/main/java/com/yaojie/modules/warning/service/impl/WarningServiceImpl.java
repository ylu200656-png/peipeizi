package com.yaojie.modules.warning.service.impl;

import com.yaojie.common.enums.ResultCode;
import com.yaojie.common.exception.BusinessException;
import com.yaojie.modules.audit.entity.OperationLog;
import com.yaojie.modules.audit.mapper.OperationLogMapper;
import com.yaojie.modules.system.entity.SysUser;
import com.yaojie.modules.system.mapper.SysUserMapper;
import com.yaojie.modules.warning.dto.WarningResolveRequest;
import com.yaojie.modules.warning.mapper.WarningRecordMapper;
import com.yaojie.modules.warning.service.WarningService;
import com.yaojie.modules.warning.vo.WarningRecordVO;
import com.yaojie.modules.warning.vo.WarningSummaryVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WarningServiceImpl implements WarningService {

    private final WarningRecordMapper warningRecordMapper;
    private final SysUserMapper sysUserMapper;
    private final OperationLogMapper operationLogMapper;

    public WarningServiceImpl(
        WarningRecordMapper warningRecordMapper,
        SysUserMapper sysUserMapper,
        OperationLogMapper operationLogMapper
    ) {
        this.warningRecordMapper = warningRecordMapper;
        this.sysUserMapper = sysUserMapper;
        this.operationLogMapper = operationLogMapper;
    }

    @Override
    public List<WarningRecordVO> list(String warningType, String status) {
        return warningRecordMapper.selectList(warningType, status);
    }

    @Override
    public WarningSummaryVO getSummary() {
        return warningRecordMapper.selectSummary();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WarningRecordVO resolve(Long id, WarningResolveRequest request, String operatorUsername, String ip) {
        WarningRecordVO warning = warningRecordMapper.selectById(id);
        if (warning == null) {
            throw new BusinessException(ResultCode.WARNING_NOT_FOUND);
        }
        if (!"OPEN".equals(warning.getStatus())) {
            throw new BusinessException(ResultCode.WARNING_ALREADY_RESOLVED);
        }

        SysUser operator = sysUserMapper.selectByUsername(operatorUsername);
        if (operator == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        int updated = warningRecordMapper.resolveWarning(id, operator.getId(), normalizeRemark(request == null ? null : request.getHandleRemark()));
        if (updated != 1) {
            throw new BusinessException(ResultCode.WARNING_ALREADY_RESOLVED);
        }

        OperationLog operationLog = new OperationLog();
        operationLog.setUserId(operator.getId());
        operationLog.setModuleName("WARNING");
        operationLog.setOperationType("RESOLVE");
        operationLog.setBusinessNo(String.valueOf(id));
        operationLog.setContent("Resolve warning " + id);
        operationLog.setIp(ip);
        operationLogMapper.insert(operationLog);

        return warningRecordMapper.selectById(id);
    }

    private String normalizeRemark(String remark) {
        if (remark == null) {
            return null;
        }
        String trimmed = remark.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
