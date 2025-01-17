package com.github.supercoding.repository.flight;

import com.github.supercoding.repository.airlineTicket.AirlineTicket;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "flightId")
@ToString
@Entity
@Table(name = "flight")
public class Flight {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flight_id")
    private Integer flightId;
    // 양방향
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id",nullable = true)
    private AirlineTicket airlineTicket;

    @Column(name = "departure_at")
    private LocalDateTime departureAt;
    @Column(name = "arrival_at")
    private LocalDateTime arrivalAt;
    @Column(name = "departure_loc")
    private String departureLoc;
    @Column(name = "arrival_loc")
    private String arrivalLoc;
    @Column(name = "flight_price")
    private Double flightPrice;
    @Column(name = "charge")
    private Double charge;

}
