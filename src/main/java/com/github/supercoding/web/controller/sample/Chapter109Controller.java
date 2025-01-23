package com.github.supercoding.web.controller.sample;

import com.github.supercoding.service.ElectronicStoreItemService;
import com.github.supercoding.web.dto.items.Item;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/sample")
@RequiredArgsConstructor
@Slf4j
public class Chapter109Controller {
    private final ElectronicStoreItemService electronicStoreItemService;

    @Operation(summary = "여러 Item prices 검색(쿼리문)")
    @GetMapping("/items-prices")
    public List<Item> findItemsByPrices(
            @Parameter(name = "Prices",description = "items max prices", example = "20000")
            HttpServletRequest httpServletRequest ){
            //@RequestParam("max") Integer maxValue){
        Integer maxValue = Integer.valueOf(httpServletRequest.getParameter("max"));
        log.info("findItemsByPrices: maxValue={}", maxValue);
        List<Item> items = electronicStoreItemService.findItemsOrderByPrices(maxValue);
        log.info("findItemsByPrices: items={}", items);
        return items;

    }
    @Operation(summary = "ID로 item 삭제")
    @DeleteMapping("/items/{id}")
    public void deleteItem(
            @PathVariable String id , HttpServletResponse httpServletResponse) throws IOException {
        electronicStoreItemService.deleteItem(id);
        String resMsg = "Object with id" + id+ " has been deleted";
        log.info("DELETE /items/"+id+" 응답: "+resMsg);
        httpServletResponse.getOutputStream().println(resMsg);
        //return "Object with id="+id+" has been deleted";
    }
}
