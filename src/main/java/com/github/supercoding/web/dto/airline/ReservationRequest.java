package com.github.supercoding.web.dto.airline;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

//요청 데이터 구조
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ReservationRequest {
    private  Integer userId;
    private  Integer airlineTicketId;

    public ReservationRequest() {
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getAirlineTicketId() {
        return airlineTicketId;
    }
}
