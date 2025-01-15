package com.github.supercoding.repository.reservations;


public interface ReservationRepository {
    Integer saveReservation(Reservation reservation);

    void updateReservationStatus(Integer reservationId, String status);

    Reservation findReservationById(Integer reservationId);
}
