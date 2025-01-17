package com.github.supercoding.repository.storeSales;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StoreSalesJpaRepository extends JpaRepository<StoreSales, Integer> {
    @Query(" SELECT s FROM StoreSales s JOIN FETCH s.itemEntities")
    List<StoreSales> findAllFetchJoin();
}
