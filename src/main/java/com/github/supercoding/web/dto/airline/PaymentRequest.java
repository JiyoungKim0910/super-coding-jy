package com.github.supercoding.web.dto.airline;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PaymentRequest {
    private List<Integer> userIds;
    private List<Integer> airlineTicketIds;

    public PaymentRequest(List<Integer> userIds, List<Integer> airlineTicketIds) {
        this.userIds = userIds;
        this.airlineTicketIds = airlineTicketIds;
    }

    public PaymentRequest() {
    }

    public List<Integer> getUserIds() {
        return userIds;
    }

    public List<Integer> getAirlineTicketIds() {
        return airlineTicketIds;
    }
}
