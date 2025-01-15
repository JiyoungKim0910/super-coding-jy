package com.github.supercoding.repository.airlineTicket;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AirlineTicketJdbcTemplateDao implements AirlineTicketRepository {
    private JdbcTemplate jdbcTemplate;

    public AirlineTicketJdbcTemplateDao(@Qualifier("jdbcTemplate2") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    static RowMapper<AirlineTicket> airlineTicketRowMapper = ((rs, rowNum) ->
            new AirlineTicket(
                    rs.getInt("ticket_id"),
                    rs.getNString("ticket_type"),
                    rs.getNString("departure_loc"),
                    rs.getNString("arrival_loc"),
                    rs.getTimestamp("departure_at"), //data type이 Date 이지만 생성자는 LocalDate이므로 생성자 타입을 변경
                    rs.getTimestamp("return_at"),
                    rs.getDouble("tax"),
                    rs.getDouble("total_price")

            ));
    static  RowMapper<AirlineTicketAndFlightInfo> airlineTicketAndFlightInfoRowMapper =((rs, rowNum) ->
            new AirlineTicketAndFlightInfo(
                    rs.getInt("A.ticket_id"),
                    rs.getDouble("F.flight_price"),
                    rs.getDouble("F.charge"),
                    rs.getDouble("A.tax"),
                    rs.getDouble("A.total_price")
            ));
    @Override
    public List<AirlineTicket> getAllAirlineTicketsWithPlaceAndTicketType(String likePlace, String ticketType) {
        return jdbcTemplate.query("SELECT * FROM airline_ticket " +
                                    "WHERE arrival_loc = ? AND ticket_type = ?", airlineTicketRowMapper, likePlace, ticketType);

    }

    @Override
    public List<AirlineTicketAndFlightInfo> getAllAirlineTicketsAndFlightInfo(Integer airlineTicketId) {
        return jdbcTemplate.query("SELECT A.ticket_id, A.tax, A.total_price, F.charge, F.flight_price " +
                                        "FROM airline_ticket A " +
                                        "INNER JOIN flight F  " +
                                        "ON A.ticket_id = F.ticket_id " +
                                        " WHERE A.ticket_id = ?", airlineTicketAndFlightInfoRowMapper,airlineTicketId);
    }
}
