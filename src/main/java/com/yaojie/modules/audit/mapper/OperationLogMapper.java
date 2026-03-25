package com.yaojie.modules.audit.mapper;

import com.yaojie.modules.audit.entity.OperationLog;
import com.yaojie.modules.audit.vo.OperationLogVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OperationLogMapper {

    int insert(OperationLog operationLog);

    List<OperationLogVO> selectList(@Param("moduleName") String moduleName, @Param("operationType") String operationType);
}
