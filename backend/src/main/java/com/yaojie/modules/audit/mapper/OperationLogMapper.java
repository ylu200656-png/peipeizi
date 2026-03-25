package com.yaojie.modules.audit.mapper;

import com.yaojie.modules.audit.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper {

    int insert(OperationLog operationLog);
}
