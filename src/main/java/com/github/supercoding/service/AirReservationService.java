package com.github.supercoding.service;


import com.github.supercoding.repository.airlineTicket.AirlineTicket;
import com.github.supercoding.repository.airlineTicket.AirlineTicketAndFlightInfo;
import com.github.supercoding.repository.airlineTicket.AirlineTicketJpaRepository;
import com.github.supercoding.repository.airlineTicket.AirlineTicketRepository;
import com.github.supercoding.repository.flight.Flight;
import com.github.supercoding.repository.flight.FlightJpaRepository;
import com.github.supercoding.repository.passenger.Passenger;
import com.github.supercoding.repository.passenger.PassengerJpaRepository;
import com.github.supercoding.repository.passenger.PassengerRepository;
import com.github.supercoding.repository.payment.Payment;
import com.github.supercoding.repository.payment.PaymentJpaRepository;
import com.github.supercoding.repository.payment.PaymentRepository;
import com.github.supercoding.repository.reservations.FlightPriceAndCharge;
import com.github.supercoding.repository.reservations.Reservation;
import com.github.supercoding.repository.reservations.ReservationJpaRepository;
import com.github.supercoding.repository.reservations.ReservationRepository;
import com.github.supercoding.repository.users.UserEntity;
import com.github.supercoding.repository.users.UserJpaRepository;
import com.github.supercoding.repository.users.UserRepository;
import com.github.supercoding.service.exceptions.InvalidValueException;
import com.github.supercoding.service.exceptions.NotAcceptException;
import com.github.supercoding.service.exceptions.NotFoundException;
import com.github.supercoding.service.mapper.TicketMapper;
import com.github.supercoding.web.dto.airline.PaymentRequest;
import com.github.supercoding.web.dto.airline.ReservationRequest;
import com.github.supercoding.web.dto.airline.ReservationResult;
import com.github.supercoding.web.dto.airline.Ticket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AirReservationService {
    private UserRepository userRepository;
    private AirlineTicketRepository airlineTicketRepository;
    private ReservationRepository reservationRepository;
    private PassengerRepository passengerRepository;
    private PaymentRepository paymentRepository;

    private final UserJpaRepository userJpaRepository;
    private final PassengerJpaRepository passengerJpaRepository;
    private final ReservationJpaRepository reservationJpaRepository;
    private final FlightJpaRepository flightJpaRepository;
    private final AirlineTicketJpaRepository airlineTicketJpaRepository;
    private final PaymentJpaRepository paymentJpaRepository;



    public List<Ticket> findUserFavoritePlaceTickets(Integer userId, String ticketType) {
        // 필요한 Repository : User, airlineTicket repository
        //1. 유저를 userId 로 가져와서, 선호하는 여행지 도출
        //2. 선호하는 여행지와 ticketType으로 AirlineTicket Table 질의해서 필요한 AirlineTicket
        //3. 이 둘의 정보를 조합해서 Ticket DTO를 만든다.

        // 티켓타입은 편도, 왕복만 가능하다
        Set<String> airlineTicketType  = new HashSet<>(Arrays.asList("편도","왕복"));

        if ( !airlineTicketType.contains(ticketType)) {
            throw new InvalidValueException("해당 TicketType "+ticketType+" 은 지원하지 않습니다.");
        }
        UserEntity userEntity = userJpaRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("해당 ID : "+userId+" 유저를 찾을 수 없습니다."));
        String likePlace = userEntity.getLikeTravelPlace();
        List<AirlineTicket> airlineTickets =
                airlineTicketJpaRepository.findAllByTicketTypeAndArrivalLocation(ticketType,likePlace);
        if(airlineTickets.isEmpty()) { throw new NotFoundException("해당 likePlace : "+likePlace+ "와 TicketType : "+ticketType+" 에 해당하는 티켓을 찾을 수 없습니다."); }
        List<Ticket> tickets = airlineTickets.stream().map(TicketMapper.INSTANCE::airlineTicketToTicket).collect(Collectors.toList());
        return tickets;
    }
    @Transactional(transactionManager = "tmJpa2")
    public ReservationResult makeReservation(ReservationRequest reservationRequest) {
        // 0. Reservation Repository, Join table(flight/airline_ticket), Passenger repository

        Integer userId = reservationRequest.getUserId();
        Integer airlineTicketId = reservationRequest.getAirlineTicketId();

        // 1. passenger
        Passenger passenger = passengerJpaRepository.findPassengerByUserUserId(userId).orElseThrow(() -> new NotFoundException("요청하신 userId: "+userId+ "에 해당하는 Passenger를 찾을 수 없습니다."));
        //Integer passengerId = passenger.getPassengerId();

        //2. price 등의 정보 불러오기
        AirlineTicket airlineTicket = airlineTicketJpaRepository.findById(airlineTicketId).orElseThrow(() -> new NotFoundException("AirlineTicket ID: "+ airlineTicketId+ "에 해당하는 항공권을 찾을 수 없습니다."));
        List<Flight> flightList = airlineTicket.getFlightList();
        //List<AirlineTicketAndFlightInfo> airlineTicketAndFlightInfo = airlineTicketRepository.getAllAirlineTicketsAndFlightInfo(airlineTicketId);
        if (!flightList.isEmpty()) {
            throw new NotFoundException("AirlineTicket ID: "+ airlineTicketId+ "에 해당하는 항공편을 찾을 수 없습니다.");
        }

        //3. reservation 생성
        Reservation reservation = new Reservation(passenger,airlineTicket);
        Boolean isSuccess = false;
        try {
            reservationJpaRepository.save(reservation);
            isSuccess = true;
        } catch (RuntimeException e) {
            throw new NotAcceptException("Reservation이 등록되는 과정이 거부되었습니다.");
        }

        //ReservationResult DTO 만들기
        List<Integer> prices = flightList.stream().map(Flight::getFlightPrice).map(Double::intValue).collect(Collectors.toList());
        List<Integer> charges = flightList.stream().map(Flight::getCharge).map(Double::intValue).collect(Collectors.toList());
        Integer tax = airlineTicket.getTax().intValue();
        Integer totalPrice = airlineTicket.getTotalPrice().intValue();

        return new ReservationResult(prices,charges,tax,totalPrice,isSuccess);

    }

    @Transactional(transactionManager = "tmJpa2")
    public String makePayment(PaymentRequest paymentRequest) {
        // userIds, ticketIds
        List<Integer> userIds = paymentRequest.getUserIds();
        List<Integer> airlineTicketIds = paymentRequest.getAirlineTicketIds();
        if(userIds == null || airlineTicketIds == null || userIds.size() != airlineTicketIds.size()){
            throw new InvalidValueException("userId와 airlineTicketId 를 다시 확인해주세요");
        }
        //passengerIds
        List<Passenger> passengerIds = passengerJpaRepository.findAllByUserUserIdIn(userIds);
        if( !passengerIds.isEmpty()) {
            throw new NotFoundException("유저: "+userIds+"를 찾을 수 없습니다.");
        }
        //airlineTicket Info
        List<AirlineTicket> airlineTickets = airlineTicketJpaRepository.findAllByTicketIdIn(airlineTicketIds);
        if (!airlineTickets.isEmpty()) {
            throw new NotFoundException("AirlineTicket ID: "+ airlineTicketIds+ "에 해당하는 항공권을 찾을 수 없습니다.");
        }
        //prices : 가격의 흐름까지는 필요 없음
        List<List<AirlineTicketAndFlightInfo>> airlineTicketAndFlightInfos = airlineTicketIds.stream().map(ticketId -> airlineTicketRepository.getAllAirlineTicketsAndFlightInfo(ticketId)).collect(Collectors.toList());

        //reservation : passenger_id, airline_ticket_id
        List<Reservation> reservationList = new ArrayList<>();
        //List<Reservation,Passenger>> reservationPairList = new ArrayList<>();
        int paymentSuccessCnt = 0;

        for(int i =0; i < userIds.size() ; i++) {
            Passenger passenger = passengerIds.get(i);
            AirlineTicket airlineTicket = airlineTickets.get(i);

            Reservation reservation = new Reservation(passenger, airlineTicket);
            Reservation savedReservation = reservationJpaRepository.save(reservation);

//            Integer reservationId = reservationRepository.saveReservation(reservation);
//            Reservation getReservation = reservationRepository.findReservationById(reservationId);
            reservationList.add(savedReservation);

        }

        //payment : passenger_id, reserve_id

        for (Reservation reservation : reservationList) {
            Passenger passenger = reservation.getPassenger();
            if(reservation == null) continue;
            if(reservation.getReservationStatus() == "확정") continue;
            Payment payment = new Payment(reservation,passenger);
            Boolean isSuccess = false;
            try {
                paymentJpaRepository.save(payment);
                isSuccess = true;
            } catch (RuntimeException e) {
                throw new NotAcceptException("결제가 처리되지 않았습니다.");
            }
            //reservation 대기 -> 확정
            if(isSuccess){
                paymentSuccessCnt++;
                Reservation updateReservation = reservationJpaRepository.findById(reservation.getReservationId()).orElseThrow(() -> new NotFoundException("예역 결과를 찾을 수 없습니다."));
                updateReservation.setReservationStatus("확정");
                //reservationRepository.updateReservationStatus(reservation.getReservationId(),"확정");
            }
        }

        return "요청하신 결제 중" + paymentSuccessCnt+"건 진행 완료 되었습니다.";

    }

    public Double findUserFlightSumPrice(Integer userId) {
        //1. flight_price, Charge 구하기
        List<FlightPriceAndCharge> flightPriceAndCharges = reservationJpaRepository.findAlightPriceAndCharge(userId);
        //2. flight_price 와 charge의 각각 합을 구하고
        Double flightSum = flightPriceAndCharges.stream().mapToDouble(FlightPriceAndCharge::getFlightPrice).sum();
        Double chargeSum = flightPriceAndCharges.stream().mapToDouble(FlightPriceAndCharge::getCharge).sum();
        //3. 두개의 합을 다시 더하고 return
        return flightSum + chargeSum ;
    }
}
