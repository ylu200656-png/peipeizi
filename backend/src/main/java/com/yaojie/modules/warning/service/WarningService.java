package com.yaojie.modules.warning.service;

import com.yaojie.modules.warning.dto.WarningResolveRequest;
import com.yaojie.modules.warning.vo.WarningRecordVO;
import com.yaojie.modules.warning.vo.WarningSummaryVO;

import java.util.List;

public interface WarningService {

    List<WarningRecordVO> list(String warningType, String status);

    WarningSummaryVO getSummary();

    WarningRecordVO resolve(Long id, WarningResolveRequest request, String operatorUsername, String ip);
}
