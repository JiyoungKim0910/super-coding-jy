package com.github.supercoding.repository.Items;

import java.util.List;

public interface ElectronicStoreItemRepository {
    List<ItemEntity> findAllItems();

    Integer saveItem(ItemEntity itemEntity);

    ItemEntity updateItem(ItemEntity itemEntity);

    ItemEntity findItemById(Integer id);

    List<ItemEntity> findItemsByIds(List<String> ids);

    void deleteItem(Integer id);

    void updateItemStock(Integer itemId, Integer stock);
}
