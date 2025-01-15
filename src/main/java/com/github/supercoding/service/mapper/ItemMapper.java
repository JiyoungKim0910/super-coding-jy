package com.github.supercoding.service.mapper;

import com.github.supercoding.repository.Items.ItemEntity;
import com.github.supercoding.web.dto.Item;
import com.github.supercoding.web.dto.ItemBody;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ItemMapper {
    //싱글톤
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);
    //메소드
    @Mapping(target = "spec.cpu", source = "cpu")
    @Mapping(target = "spec.capacity", source = "capacity")
    Item itemEntityToItem(ItemEntity itemEntity);

//    @Mapping(target = "stock", constant = "0")
//    @Mapping(source = "itemBody.spec.cpu", target = "cpu")
//    @Mapping(source = "itemBody.spec.capacity", target = "capacity")
    @Mapping(target = "cpu", source = "itemBody.spec.cpu")
    @Mapping(target = "capacity", source = "itemBody.spec.capacity")
    @Mapping(target = "storeSales" , ignore = true)
    @Mapping(target = "stock" ,expression = "java(0)")
    ItemEntity idAndItemBodyToItemEntity(Object o, ItemBody itemBody);
}
