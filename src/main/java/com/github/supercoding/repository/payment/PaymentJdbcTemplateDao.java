package com.github.supercoding.repository.payment;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentJdbcTemplateDao implements PaymentRepository{
    JdbcTemplate jdbcTemplate;
    public PaymentJdbcTemplateDao(@Qualifier("jdbcTemplate2") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Boolean savePayment(Payment payment) {
        Integer rowNum = jdbcTemplate.update("INSERT INTO payment (passenger_id,reservation_id,pay_at) VALUES (?,?,?)",
                payment.getPassengerId(), payment.getReservationId(), payment.getPayAt());
        return rowNum > 0;
    }
}
