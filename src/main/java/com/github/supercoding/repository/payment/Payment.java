package com.github.supercoding.repository.payment;

import java.time.LocalDateTime;
import java.util.Objects;

public class Payment {
    private Integer paymentId;
    private Integer passengerId;
    private Integer reservationId;
    private LocalDateTime payAt;

    public Payment(Integer reservationId, Integer passengerId) {
        this.passengerId = passengerId;
        this.reservationId = reservationId;
        this.payAt = LocalDateTime.now();
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public Integer getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Integer passengerId) {
        this.passengerId = passengerId;
    }

    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public LocalDateTime getPayAt() {
        return payAt;
    }

    public void setPayAt(LocalDateTime payAt) {
        this.payAt = payAt;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Payment)) return false;
        Payment payment = (Payment) o;
        return Objects.equals(paymentId, payment.paymentId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(paymentId);
    }
}
