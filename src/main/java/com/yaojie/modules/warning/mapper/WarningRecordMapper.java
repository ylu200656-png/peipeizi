package com.yaojie.modules.warning.mapper;

import com.yaojie.modules.warning.vo.WarningRecordVO;
import com.yaojie.modules.warning.vo.WarningSummaryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WarningRecordMapper {

    int deleteOpenWarnings();

    int insertLowStockWarnings();

    int insertExpiredWarnings();

    int insertExpirySoonWarnings();

    List<WarningRecordVO> selectList(@Param("warningType") String warningType, @Param("status") String status);

    List<WarningRecordVO> selectLatestList(@Param("limit") Integer limit);

    WarningSummaryVO selectSummary();
}
