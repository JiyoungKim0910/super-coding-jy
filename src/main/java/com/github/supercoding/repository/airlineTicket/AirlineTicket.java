package com.github.supercoding.repository.airlineTicket;

import com.github.supercoding.repository.flight.Flight;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "ticketId")
@Entity
@Builder
@Table(name = "airline_ticket")
public class AirlineTicket {
    @Id @Column(name = "ticket_id")
    private Integer ticketId;
    @Column(name = "ticket_type", length = 5)
    private String ticketType;
    @Column(name = "departure_loc",length = 20)
    private String departureLocation;
    @Column(name = "arrival_loc",length = 20)
    private String arrivalLocation;
    @Column(name = "departure_at", nullable = false)
    private LocalDateTime departureAt;
    @Column(name = "return_at", nullable = false)
    private LocalDateTime returnAt;
    @Column(name = "tax")
    private  Double tax;
    @Column(name = "total_price")
    private  Double totalPrice;
    //  FK 양방향으로 임의 지정
    @OneToMany(mappedBy = "airlineTicket" )
    private List<Flight> flightList;

    public AirlineTicket(Integer ticketId, String ticketType, String departureLocation, String arrivalLocation, Timestamp departureAt, Timestamp returnAt, Double tax, Double totalPrice) {
        this.ticketId = ticketId;
        this.ticketType = ticketType;
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.departureAt = departureAt.toLocalDateTime();
        this.returnAt = returnAt.toLocalDateTime();
        this.tax = tax;
        this.totalPrice = totalPrice;
    }


}
