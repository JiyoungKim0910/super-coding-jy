package com.github.supercoding.web.controller;

import com.github.supercoding.repository.userDetails.CustomUserDetails;
import com.github.supercoding.service.AirReservationService;
import com.github.supercoding.web.dto.airline.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/v1/api/air-reservation")
@RequiredArgsConstructor
@Slf4j
public class AirReservationController {
    private final AirReservationService airReservationService;


    @Operation(summary = "선호하는 Ticket 탐색")
    @GetMapping("tickets")
    @ResponseStatus(HttpStatus.OK)
    public TicketResponse findAirPlaneTickets (
            //@Parameter(name = "user-Id", description = "유저 ID", example = "1") @RequestParam("user-Id")Integer userId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Parameter(name = "airline-ticket-type", description = "항공권 타입", example = "왕복|편도") @RequestParam("airline-ticket-type")String ticketType){

        Integer userId = customUserDetails.getUserId();
        List<Ticket> tickets = airReservationService.findUserFavoritePlaceTickets(userId, ticketType);
        return new TicketResponse(tickets);

    }
    @Operation(summary = "User와 Ticket ID로 예약 진행")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("reservations")
    public ReservationResult makeReservation (@RequestBody ReservationRequest reservationRequest){

        ReservationResult reservationResult = airReservationService.makeReservation(reservationRequest);
        return reservationResult;
    }
    @Operation(summary = "Reservation 결제")
    @PostMapping("/payments")
    public String makePayment(@RequestBody PaymentRequest paymentRequest){
        return airReservationService.makePayment(paymentRequest);

    }

    @Operation(summary = "userId의 예약한 항공편과 수수료 병합")
    @GetMapping("/users-sum-price")
    public Double findUserFlightSumPrice(
            @Parameter(name = "user-id", description = "유저ID",example = "1")
            @RequestParam("user-id") Integer userId
    ){
        Double sum = airReservationService.findUserFlightSumPrice(userId);
        return sum;
    }

    @Operation(summary = "항공권의 왕복|편도에 따라 달라지는 Pageable하게 반환하는 API")
    @GetMapping("/flight-pageable")
    public Page<FlightInfo> findAllFlightInfo(
            @Parameter(name = "type",description = "항공권 Type", example = "왕복")
            @RequestParam("type") String ticketType, Pageable pageable
    ){
        return airReservationService.findAllFlightInfoByTicketType(ticketType,pageable);
    }

    @Operation(summary = "유저의 예약한 항공편들의 목적지")
    @GetMapping("/username-arrival-location")
    public List<String> findUserReservedFlightsArrivalLocation(
            @Parameter(name = "username", description = "유저 이름", example = "김영희")
            @RequestParam("username") String userName
    ){
        return airReservationService.findReservedFlightArrivalLocation(userName);
    }


}
