package com.github.supercoding.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItemBody {
    @Schema(name = "name", description = "Item 이름", example = "Dell XPS 15") private String name;
    @Schema(name = "type", description = "Item 종류", example = "Laptop") private String type;
    @Schema(name = "price", description = "Item 가격", example = "125000") private Integer price;
    private Spec spec;

}
