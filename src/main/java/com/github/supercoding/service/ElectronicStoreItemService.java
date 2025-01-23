package com.github.supercoding.service;

import com.github.supercoding.repository.Items.ElectronicStoreItemJpaRepository;
import com.github.supercoding.repository.Items.ItemEntity;
import com.github.supercoding.repository.storeSales.StoreSales;
import com.github.supercoding.repository.storeSales.StoreSalesJpaRepository;
import com.github.supercoding.service.exceptions.NotAcceptException;
import com.github.supercoding.service.exceptions.NotFoundException;
import com.github.supercoding.service.mapper.ItemMapper;
import com.github.supercoding.web.dto.items.BuyOrder;
import com.github.supercoding.web.dto.items.Item;
import com.github.supercoding.web.dto.items.ItemBody;
import com.github.supercoding.web.dto.items.StoreInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class ElectronicStoreItemService {
    //private final ElectronicStoreItemRepository electronicStoreItemRepository;
    //private final StoreSalesRepository storeSalesRepository;

    private final ElectronicStoreItemJpaRepository electronicStoreItemJpaRepository;
    private final StoreSalesJpaRepository storeSalesJpaRepository;

//    public ElectronicStoreItemService(ElectronicStoreItemRepository electronicStoreItemRepository, StoreSalesRepository storeSalesRepository) {
//        this.electronicStoreItemRepository = electronicStoreItemRepository;
//        this.storeSalesRepository = storeSalesRepository;
//    }
    @Cacheable(value = "items", key = "#root.methodName")
    public List<Item> findAllItems() {
        //List<ItemEntity> itemEntities = electronicStoreItemRepository.findAllItems();
        List<ItemEntity> itemEntities = electronicStoreItemJpaRepository.findAll();
        //return itemEntities.stream().map(Item::new).collect(Collectors.toList());
        return itemEntities.stream().map(ItemMapper.INSTANCE::itemEntityToItem).collect(Collectors.toList());
    }
    @CacheEvict(value = "items", allEntries = true)
    public Integer saveItem(ItemBody itemBody) {
        ItemEntity itemEntity = ItemMapper.INSTANCE.idAndItemBodyToItemEntity(null,itemBody);
        //ItemEntity itemEntity = new ItemEntity(null, itemBody.getName(),itemBody.getType(),itemBody.getPrice(),itemBody.getSpec().getCpu(),itemBody.getSpec().getCapacity());
        ItemEntity createdItemEntity ;
        try{
            createdItemEntity = electronicStoreItemJpaRepository.save(itemEntity);
        } catch (RuntimeException e){
            throw new NotAcceptException("Item을 저장하는 도중에 Error가 발생하였습니다.");
        }
        return createdItemEntity.getId();
    }
    @Cacheable(value = "items", key = "#id")
    public Item findItemById(String id) {
        Integer itemId = Integer.valueOf(id);
        ItemEntity itemEntities = electronicStoreItemJpaRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("해당 ID : " + itemId+ "의 정보를 찾을 수 없습니다."));
        Item itemFounded = ItemMapper.INSTANCE.itemEntityToItem(itemEntities);
        return itemFounded;
    }
    @Cacheable(value = "items", key = "#ids")
    public List<Item> findItemsByIds(List<String> ids) {
        List<ItemEntity> itemEntities = electronicStoreItemJpaRepository.findItemEntitiesByIdIn(ids);
        if(itemEntities.isEmpty()){ throw new NotFoundException("Items 들을 찾을 수가 없습니다.");}

        List<Item> itemsFounded = itemEntities.stream()
                .map(ItemMapper.INSTANCE::itemEntityToItem)
                //.filter(item -> ids.contains(item.getId()))
                .collect(Collectors.toList());
        return itemsFounded;
    }
    @CacheEvict(value = "items", allEntries = true)
    public void deleteItem(String id) {
        Integer itemId = Integer.valueOf(id);
        electronicStoreItemJpaRepository.deleteById(itemId);

    }
    @CacheEvict(value = "items", allEntries = true)
    @Transactional(transactionManager = "tmJpa1")
    public Item updateItem(String id, ItemBody itemBody) {
        Integer idInt = Integer.valueOf(id);
//        ItemEntity itemEntity = new ItemEntity(idInt, itemBody.getName(),itemBody.getType(),
//                itemBody.getPrice(),itemBody.getSpec().getCpu(),itemBody.getSpec().getCapacity());
//        ItemEntity itemEntityUpdated = electronicStoreItemRepository.updateItem(itemEntity);
        ItemEntity itemEntityUpdated = electronicStoreItemJpaRepository.findById(idInt)
                .orElseThrow(() -> new NotFoundException("해당 ID : " + idInt+ "의 정보를 찾을 수 없습니다."));
        itemEntityUpdated.setItemBody(itemBody);
        return ItemMapper.INSTANCE.itemEntityToItem(itemEntityUpdated);
    }
    @Transactional(transactionManager = "tmJpa1") //트랜젝션 추가 : 에러가 발생되면 원상복귀
    public Integer buyItems(BuyOrder buyOrder) {
        //1. Buy Order 에서 상품 id 와 수량을 얻어낸다.
        //2. 상품을 조회하여 수량이 얼마나 있는지 계산한다.
        //3. 상품의 수량과 가격을 가지고 계산하여 총 가격을 구한다.
        //4. 상품의 재고에 기존 계산한 재고를 구매하는 수량을 뺀다.
        //5. 상품 구매하는 수량 * 가격 만큼 가게 매상으로 올린다.
        //단, 재고가 아예 없거나 매장을 찾을 수 없으면 살 수 없다.

        Integer itemId = buyOrder.getItemId();
        Integer itemNums = buyOrder.getItemNums();

        ItemEntity itemEntity = electronicStoreItemJpaRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("해당 ID : " + itemId+ "의 정보를 찾을 수 없습니다."));

        //예외처리
        if ( itemEntity.getStoreSales().isEmpty()  ) throw new NotFoundException("매장을 찾을 수 없습니다.");
        if ( itemEntity.getStock() <= 0 ) throw  new NotAcceptException("상품의 재고가 없습니다.");

        Integer availableBuyItemNums;
        if ( itemNums > itemEntity.getStock() ) availableBuyItemNums = itemEntity.getStock();
        else availableBuyItemNums = itemNums;

        //구매금액
        Integer totalPrice = availableBuyItemNums * itemEntity.getPrice();

        //Item 재고 감소
        //electronicStoreItemRepository.updateItemStock(itemId,itemEntity.getStock() - availableBuyItemNums );
        itemEntity.setStock(itemEntity.getStock() - availableBuyItemNums );
        //원자성을 구현하기 위해 트랜젝션을 구현해야함

        //매장 매상 추가
        //매장 매상
        StoreSales storeSales = itemEntity.getStoreSales().orElseThrow(() -> new NotFoundException("가게 정보를 찾을 수 없습니다."));
        //storeSalesRepository.updateSalesAmount(itemEntity.getStoreId(),storeSales.getAmount()+totalPrice);
        storeSales.setAmount(storeSales.getAmount()+totalPrice);
        return availableBuyItemNums;
    }

    public List<Item> findItemsByTypes(List<String> types) {
        List<ItemEntity> itemEntities = electronicStoreItemJpaRepository.findItemEntitiesByTypeIn(types);
        return itemEntities.stream().map(ItemMapper.INSTANCE::itemEntityToItem).collect(Collectors.toList());
    }

    public List<Item> findItemsOrderByPrices(Integer maxValue) {
        List<ItemEntity> itemEntities = electronicStoreItemJpaRepository.findItemEntitiesByPriceLessThanEqualOrderByPriceAsc(maxValue);
        return itemEntities.stream().map(ItemMapper.INSTANCE::itemEntityToItem).collect(Collectors.toList());
    }

    public Page<Item> findAllWithPageable(Pageable pageable) {
        Page<ItemEntity> itemEntities = electronicStoreItemJpaRepository.findAll(pageable);
        return itemEntities.map(ItemMapper.INSTANCE::itemEntityToItem);
    }

    public Page<Item> findAllWithPageable2(List<String> types, Pageable pageable) {
        Page<ItemEntity> itemEntities = electronicStoreItemJpaRepository.findAllByTypeIn(types, pageable);
        return itemEntities.map(ItemMapper.INSTANCE::itemEntityToItem);
    }

    @Transactional(transactionManager = "tmJpa1")
    public List<StoreInfo> findAllStoreInfo() {
        List<StoreSales> storeSales = storeSalesJpaRepository.findAllFetchJoin();
        //N+1문제 발생
        log.info("=================================N + 1 확인 용 로그 ===========================");
        List<StoreInfo> storeInfos = storeSales.stream().map(StoreInfo::new).collect(Collectors.toList());
        return storeInfos;
    }
}
