package com.github.supercoding.repository.reservations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Integer> {

    @Query("SELECT new com.github.supercoding.repository.reservations.FlightPriceAndCharge (f.flightPrice,f.charge) " +
            "FROM Reservation r " +
            "JOIN r.passenger p " +
            "JOIN r.airlineTicket a " +
            "JOIN a.flightList f " +
            "WHERE p.user.userId = :userId")
    List<FlightPriceAndCharge> findAlightPriceAndCharge(Integer userId);

    @Query("SELECT DISTINCT a.arrivalLocation " +
            "FROM Reservation r " +
            "JOIN r.passenger p " +
            "JOIN r.airlineTicket a " +
            "WHERE p.user.userName = :userName " )
    List<String> findReservedArrivalLocations(String userName);
}
