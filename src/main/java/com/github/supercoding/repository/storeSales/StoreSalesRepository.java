package com.github.supercoding.repository.storeSales;

public interface StoreSalesRepository {

    StoreSales findStoreSalesById(Integer storeId);

    void updateSalesAmount(Integer storeId, Integer amount);
}
