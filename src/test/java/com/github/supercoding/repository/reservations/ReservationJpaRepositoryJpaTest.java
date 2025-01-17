package com.github.supercoding.repository.reservations;

import com.github.supercoding.repository.airlineTicket.AirlineTicket;
import com.github.supercoding.repository.airlineTicket.AirlineTicketJpaRepository;
import com.github.supercoding.repository.passenger.Passenger;
import com.github.supercoding.repository.passenger.PassengerJpaRepository;
import com.github.supercoding.service.AirReservationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest //slice test => Dao layer/ jpa만 사용하고 있는 slice test
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
class ReservationJpaRepositoryJpaTest {
    //@Autowired
    //private AirReservationService airReservationService;
    //DataJpaTest slice test이기 때문에 해당 레포지토리만 가져올 수 있다.

    @Autowired
    private ReservationJpaRepository reservationJpaRepository;
    @Autowired
    private PassengerJpaRepository passengerJpaRepository;
    @Autowired
    private AirlineTicketJpaRepository airlineTicketJpaRepository;

    @DisplayName("findAlightPriceAndCharge")
    @Test
    void findAlightPriceAndCharge() {
        //given
        Integer userId = 10;
        //when
        List<FlightPriceAndCharge> flightPriceAndCharges = reservationJpaRepository.findAlightPriceAndCharge(userId);
        //then
        log.info("flightPriceAndCharges: {}", flightPriceAndCharges);
    }
    @DisplayName("Reservation 예약진행")
    @Test
    void savaReservation() {
        //given
        Integer userId = 10;
        Integer ticketId = 5;
        Passenger passenger = passengerJpaRepository.findPassengersByPassengerId(userId);
        AirlineTicket airlineTicket = airlineTicketJpaRepository.findById(ticketId).get();
        //when
        Reservation reservation = new Reservation(passenger, airlineTicket);
        Reservation savedReservation = reservationJpaRepository.save(reservation);
        //then
        log.info("savedReservation: {}", savedReservation);
        assertEquals(savedReservation.getPassenger(), passenger);
        assertEquals(savedReservation.getAirlineTicket(), airlineTicket);
    }
}