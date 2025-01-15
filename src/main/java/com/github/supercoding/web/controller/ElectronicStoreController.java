package com.github.supercoding.web.controller;

import com.github.supercoding.service.ElectronicStoreItemService;
import com.github.supercoding.web.dto.BuyOrder;
import com.github.supercoding.web.dto.Item;
import com.github.supercoding.web.dto.ItemBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ElectronicStoreController {
    private final Logger logger = LoggerFactory.getLogger((this.getClass()));
    //bean 생성자
    private final ElectronicStoreItemService electronicStoreItemService;

    // bena 주입
//    public ElectronicStoreController(ElectronicStoreItemService electronicStoreItemService) {
//        this.electronicStoreItemService = electronicStoreItemService;
//    }
    @Operation(summary = "모든 Items를 검색하는 API ")
    @GetMapping("/items")
    public List<Item> findAllItem(){
        logger.info("GET /items 요청이 들어왔습니다.");
        List<Item> items = electronicStoreItemService.findAllItems();
        logger.info("Get /items 응답: "+items);
        return items;

    }
    @Operation(summary = "모든 Item 등록")
    @PostMapping("/items")
    public String registerItem(@RequestBody ItemBody itemBody){
        Integer itemID = electronicStoreItemService.saveItem(itemBody);
        return "ID: "+itemID;
    }
    @Operation(summary = "모든 Item id로 검색")
    @GetMapping("/items/{id}")
    public Item findItemByPathId(
            @Parameter(name = "id", description = "item ID", example = "1")
            @PathVariable String id){
        return electronicStoreItemService.findItemById(id);
    }
    @GetMapping("/items-query")
    public Item findItemByQueryId(@RequestParam("id") String id){
        return electronicStoreItemService.findItemById(id);
    }

    @GetMapping("/items-queries")
    public List<Item> findItemsByQueryIds(@RequestParam("id") List<String> ids){
        logger.info("/items-queries 요청 ids: "+ ids);
        List<Item> items = electronicStoreItemService.findItemsByIds(ids);
        logger.info("/items-queries 응답 : "+ items);
        return electronicStoreItemService.findItemsByIds(ids);
    }

    @DeleteMapping("/items/{id}")
    public String deleteItem(@PathVariable String id){

        electronicStoreItemService.deleteItem(id);
        return "Object with id="+id+" has been deleted";
    }
    @PutMapping("/items/{id}")
    public Item updateItem(@PathVariable String id, @RequestBody ItemBody itemBody){
        return electronicStoreItemService.updateItem(id,itemBody);
    }
    @PostMapping("/items/buy")
    public String buyItem(@RequestBody BuyOrder buyOrder){
        Integer orderItemNums = electronicStoreItemService.buyItems(buyOrder);
        return "요청하신 Item 중 "+ orderItemNums +"개를 구매 하였습니다.";
    }
    @Operation(summary = "여러 Item types 검색(쿼리문)")
    @GetMapping("/items-types")
    public List<Item> findItemsByTypes(
            @Parameter(name = "Type",description = "items type", example = "phone")
            @RequestParam("type") List<String> types){
        log.info("/items-types 요청 type: "+ types);
        List<Item> items = electronicStoreItemService.findItemsByTypes(types);
        logger.info("/items-types 응답 : "+ items);
        return items;
    }
    @Operation(summary = "여러 Item prices 검색(쿼리문)")
    @GetMapping("/items-prices")
    public List<Item> findItemsByPrices(
            @Parameter(name = "Prices",description = "items max prices", example = "20000")
            @RequestParam("max") Integer maxValue){
        return electronicStoreItemService.findItemsOrderByPrices(maxValue);

    }
    @Operation(summary = "pagination 지원")
    @GetMapping("/items-page")
    public Page<Item> findItemsPagination(Pageable pageable){
        return electronicStoreItemService.findAllWithPageable(pageable);
    }

    @Operation(summary = "pagination 지원2")
    @GetMapping("/items-types-page")
    public Page<Item> findItemsPagination(@RequestParam("type") List<String> types, Pageable pageable){
        return electronicStoreItemService.findAllWithPageable2(types, pageable);
    }
}
