package com.github.supercoding.service.mapper;

import com.github.supercoding.repository.flight.Flight;
import com.github.supercoding.web.dto.airline.FlightInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FlightMapper {
    FlightMapper INSTANCE = Mappers.getMapper(FlightMapper.class);

    FlightInfo flightToFlightInfo(Flight flight);

}
