package com.github.supercoding.repository.reservations;

import com.github.supercoding.repository.airlineTicket.AirlineTicket;
import com.github.supercoding.repository.passenger.Passenger;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "reservationId")
@Entity
@Table(name = "reservation")

public class Reservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Integer reservationId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "airline_ticket_id")
    private AirlineTicket airlineTicket;

    @Column(name = "reservation_status",length = 10)
    private String reservationStatus;
    @Column(name = "reserve_at")
    private LocalDateTime reserveAt;

    public Reservation(Passenger passenger, AirlineTicket airlineTicket) {
        this.passenger = passenger;
        this.airlineTicket = airlineTicket;
        this.reservationStatus = "대기";
        this.reserveAt = LocalDateTime.now();
    }
    public Reservation(Integer reservationId, Passenger passenger, AirlineTicket airlineTicket, String reservationStatus, Date reserveAt) {
        this.reservationId = reservationId;
        this.passenger = passenger;
        this.airlineTicket = airlineTicket;
        this.reservationStatus = reservationStatus;
        this.reserveAt = reserveAt.toLocalDate().atStartOfDay();
    }

}
