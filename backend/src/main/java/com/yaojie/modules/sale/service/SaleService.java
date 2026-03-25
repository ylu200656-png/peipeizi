package com.yaojie.modules.sale.service;

import com.yaojie.modules.sale.dto.SaleCreateRequest;
import com.yaojie.modules.sale.vo.SaleOrderVO;

import java.util.List;

public interface SaleService {

    SaleOrderVO create(SaleCreateRequest request, String username, String ip);

    List<SaleOrderVO> list();

    SaleOrderVO getById(Long id);
}
