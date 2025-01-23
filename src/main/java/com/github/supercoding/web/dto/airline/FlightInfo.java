package com.github.supercoding.web.dto.airline;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class FlightInfo {
    private String flightId;
    private LocalDateTime departureAt;
    private LocalDateTime arrivalAt;
    private String departureLoc;
    private String arrivalLoc;

}
