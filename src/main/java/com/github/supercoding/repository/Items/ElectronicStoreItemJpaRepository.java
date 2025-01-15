package com.github.supercoding.repository.Items;


import com.github.supercoding.web.dto.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ElectronicStoreItemJpaRepository extends JpaRepository<ItemEntity,Integer> {
    List<ItemEntity> findItemEntitiesByIdIn(List<String> ids);

    List<ItemEntity> findItemEntitiesByTypeIn(List<String> types);

    List<ItemEntity> findItemEntitiesByPriceLessThanEqualOrderByPriceAsc(Integer maxValue);

    Page<ItemEntity> findAllByTypeIn(List<String> types, Pageable pageable);
}
