package com.github.supercoding.repository.passenger;

import com.github.supercoding.service.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PassengerJdbcTemplateDao implements PassengerRepository{
    private JdbcTemplate jdbcTemplate;

    public PassengerJdbcTemplateDao(@Qualifier("jdbcTemplate2") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    private RowMapper<Passenger> passengerRowMapper = ((rs, rowNum) ->
            new Passenger(
                    rs.getInt("passenger_id"),
                    rs.getInt("user_id"),
                    rs.getNString("passport_num")
            ));

    @Override
    public Optional<Passenger> findPassengerByUserId(Integer userId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM passenger WHERE user_id = ?", passengerRowMapper, userId));
        } catch (Exception e){
            return Optional.empty();

        }
    }
}
