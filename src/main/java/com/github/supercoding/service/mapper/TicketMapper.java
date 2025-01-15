package com.github.supercoding.service.mapper;

import com.github.supercoding.repository.airlineTicket.AirlineTicket;
import com.github.supercoding.web.dto.airline.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper
public interface TicketMapper {
    TicketMapper INSTANCE = Mappers.getMapper(TicketMapper.class);
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    //메소드
    //AirlineTicket -> Ticket
    @Mapping(target = "depart", source = "departureLocation")
    @Mapping(target = "arrival", source = "arrivalLocation")
    @Mapping(target = "departureTime" ,source = "departureAt" , qualifiedByName = "convert")
    @Mapping(target = "returnTime", source = "returnAt", qualifiedByName = "convert")
    Ticket airlineTicketToTicket(AirlineTicket airlineTicket);

    @Named("convert")
    static String localDateTimeToString(LocalDateTime localDateTime){
        return localDateTime.format(formatter);
    }

}
