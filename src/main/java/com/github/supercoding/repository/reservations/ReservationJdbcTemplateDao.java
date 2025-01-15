package com.github.supercoding.repository.reservations;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;


@Repository
public class ReservationJdbcTemplateDao implements ReservationRepository {
    private JdbcTemplate jdbcTemplate;

    public ReservationJdbcTemplateDao(@Qualifier("jdbcTemplate2") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    static RowMapper<Reservation> reservationRowMapper = ((rs, rowNum) ->
            new Reservation(
                    rs.getInt("reservation_id"),
                    rs.getInt("passenger_id"),
                    rs.getInt("airline_ticket_id"),
                    rs.getNString("reservation_status"),
                    rs.getDate("reserve_at")
            ));

    @Override
    public Integer saveReservation(Reservation reservation) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            String sql = "INSERT INTO reservation(passenger_id, airline_ticket_id, reservation_status, reserve_at) VALUES (? ,? , ?, ? )";
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, reservation.getPassengerId());
                ps.setInt(2, reservation.getAirlineTicketId());
                ps.setString(3, reservation.getReservationStatus());
                ps.setTimestamp(4, Timestamp.valueOf(reservation.getReserveAt()));
                return ps;
            }, keyHolder);
            // Insert 후 auto incerement 된 PK key 가져오기
            return keyHolder.getKey().intValue();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void updateReservationStatus(Integer reservationId, String status) {
        jdbcTemplate.update("UPDATE reservation" +
                " SET reservation_status = ? " +
                " WHERE reservation_id = ?", status, reservationId);

    }

    @Override
    public Reservation findReservationById(Integer reservationId) {
        return jdbcTemplate.queryForObject("SELECT * FROM reservation WHERE reservation_id = ? ", reservationRowMapper, reservationId);
    }
}
