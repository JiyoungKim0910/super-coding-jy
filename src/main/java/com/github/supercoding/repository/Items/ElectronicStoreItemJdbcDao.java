package com.github.supercoding.repository.Items;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class ElectronicStoreItemJdbcDao implements ElectronicStoreItemRepository {
    // Bean 생성
    private JdbcTemplate jdbcTemplate;
    static RowMapper<ItemEntity> itemEntityRowMapper = ((rs, rowNum) ->
            new ItemEntity.ItemEntityBuilder() // Lombok의 Builder를 이용하여 매핑
                    .id(rs.getInt("id"))
                    .name(rs.getNString("name"))
                    .type(rs.getNString("type"))
                    .price(rs.getInt("price"))
                    //.storeId(rs.getInt("store_id"))
                    .stock(rs.getInt("stock"))
                    .cpu(rs.getNString("cpu"))
                    .capacity(rs.getNString("capacity"))
                    .build()
            );
    //Qualifier 사용시에는 Lombok 사용불가
    public ElectronicStoreItemJdbcDao(@Qualifier("jdbcTemplate1") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ItemEntity> findAllItems() {
        List<ItemEntity> itemEntities =  jdbcTemplate.query("SELECT * FROM item",itemEntityRowMapper);
        return itemEntities;
    }

    @Override
    public Integer saveItem(ItemEntity itemEntity) {
        jdbcTemplate.update("INSERT INTO item(name,type,price,cpu,capacity) VALUES (?,?,?,?,?)",
                itemEntity.getName(),itemEntity.getType(),itemEntity.getPrice(),
                itemEntity.getCpu(),itemEntity.getCapacity());
        ItemEntity itemEntityFound = jdbcTemplate.queryForObject("SELECT * FROM item WHERE name = ?",itemEntityRowMapper,itemEntity.getName());
        return itemEntityFound.getId();
    }

    @Override
    public ItemEntity updateItem(ItemEntity itemEntity) {
        jdbcTemplate.update("UPDATE item " +
                "SET name=?, type=? , price=?, cpu=?, capacity=? " +
                "WHERE id=?",itemEntity.getName(),itemEntity.getType(),itemEntity.getPrice(),
                itemEntity.getCpu(),itemEntity.getCapacity(),itemEntity.getId());

        return jdbcTemplate.queryForObject("SELECT * FROM item WHERE id = ?",itemEntityRowMapper,itemEntity.getId());
    }

    @Override
    public ItemEntity findItemById(Integer id) {
        return jdbcTemplate.queryForObject("SELECT * FROM item WHERE id = ?",itemEntityRowMapper,id);
    }

    @Override
    public List<ItemEntity> findItemsByIds(List<String> ids) {
        //Set<String> idSet = ids.stream().collect(Collectors.t());
        Object[] params = ids.toArray();
        String sql = "SELECT * FROM item WHERE id in (%s)";
        List<ItemEntity> itemEntities = jdbcTemplate.query(sql,itemEntityRowMapper,params);
        return itemEntities;
    }

    @Override
    public void deleteItem(Integer id) {
        jdbcTemplate.update("DELETE FROM item WHERE id = ?",id);

    }

    @Override
    public void updateItemStock(Integer itemId, Integer stock) {
        jdbcTemplate.update("UPDATE item SET stock = ? WHERE id = ?",stock,itemId);
    }
}
