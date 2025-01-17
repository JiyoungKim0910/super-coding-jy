package com.github.supercoding.repository.airlineTicket;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AirlineTicketJpaRepository  extends JpaRepository<AirlineTicket, Integer> {
    List<AirlineTicket> findAllByTicketTypeAndArrivalLocation(String ticketType, String likePlace);
    List<AirlineTicket> findAllByTicketIdIn(List<Integer> ticketIds);
}
