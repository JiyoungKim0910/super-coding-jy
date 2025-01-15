package com.github.supercoding.repository.airlineTicket;

import java.util.List;

public interface AirlineTicketRepository {
    List<AirlineTicket> getAllAirlineTicketsWithPlaceAndTicketType(String likePlace, String ticketType);

    List<AirlineTicketAndFlightInfo> getAllAirlineTicketsAndFlightInfo(Integer airlineTicketId);

}
