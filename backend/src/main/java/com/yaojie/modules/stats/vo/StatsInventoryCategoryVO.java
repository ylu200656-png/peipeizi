package com.yaojie.modules.stats.vo;

import lombok.Data;

@Data
public class StatsInventoryCategoryVO {

    private Long categoryId;
    private String categoryName;
    private Integer availableQuantity;
    private Integer batchCount;
}
