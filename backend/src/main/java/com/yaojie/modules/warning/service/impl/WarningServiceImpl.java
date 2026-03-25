package com.yaojie.modules.warning.service.impl;

import com.yaojie.modules.warning.mapper.WarningRecordMapper;
import com.yaojie.modules.warning.service.WarningService;
import com.yaojie.modules.warning.vo.WarningRecordVO;
import com.yaojie.modules.warning.vo.WarningSummaryVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarningServiceImpl implements WarningService {

    private final WarningRecordMapper warningRecordMapper;

    public WarningServiceImpl(WarningRecordMapper warningRecordMapper) {
        this.warningRecordMapper = warningRecordMapper;
    }

    @Override
    public List<WarningRecordVO> list(String warningType, String status) {
        return warningRecordMapper.selectList(warningType, status);
    }

    @Override
    public WarningSummaryVO getSummary() {
        return warningRecordMapper.selectSummary();
    }
}
