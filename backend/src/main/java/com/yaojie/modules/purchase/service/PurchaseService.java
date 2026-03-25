package com.yaojie.modules.purchase.service;

import com.yaojie.modules.purchase.dto.PurchaseCreateRequest;
import com.yaojie.modules.purchase.vo.PurchaseOrderVO;

import java.util.List;

public interface PurchaseService {

    PurchaseOrderVO create(PurchaseCreateRequest request, String username, String ip);

    List<PurchaseOrderVO> list();

    PurchaseOrderVO getById(Long id);
}
