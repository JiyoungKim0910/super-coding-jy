package com.github.supercoding.repository.payment;

import com.github.supercoding.repository.passenger.Passenger;
import com.github.supercoding.repository.reservations.Reservation;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "pamentId")
@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer paymentId;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;
    @Column(name = "pay_at")
    private LocalDateTime payAt;

    public Payment(Reservation reservation, Passenger passenger) {
        this.passenger = passenger;
        this.reservation = reservation;
        this.payAt = LocalDateTime.now();
    }

}
