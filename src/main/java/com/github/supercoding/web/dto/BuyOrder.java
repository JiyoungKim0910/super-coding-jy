package com.github.supercoding.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BuyOrder {
    @Schema(name = "itemId", description = "Item ID", example = "1") private Integer itemId;
    @Schema(name = "itemNum", description = "Item 구매 수량", example = "2") private Integer itemNums;


}
