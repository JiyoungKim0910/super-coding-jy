package com.github.supercoding.web.dto.items;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Spec {
    @Schema(name = "cpu", description = "Item CPU", example = "Google Tensor") private String cpu;
    @Schema(name = "capacity", description = "Item 용량 Spec", example = "256GB")private String capacity;



}
