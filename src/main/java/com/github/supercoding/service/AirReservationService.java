package com.github.supercoding.service;

import com.github.supercoding.repository.airlineTicket.AirlineTicket;
import com.github.supercoding.repository.airlineTicket.AirlineTicketAndFlightInfo;
import com.github.supercoding.repository.airlineTicket.AirlineTicketRepository;
import com.github.supercoding.repository.passenger.Passenger;
import com.github.supercoding.repository.passenger.PassengerRepository;
import com.github.supercoding.repository.payment.Payment;
import com.github.supercoding.repository.payment.PaymentRepository;
import com.github.supercoding.repository.reservations.Reservation;
import com.github.supercoding.repository.reservations.ReservationRepository;
import com.github.supercoding.repository.users.UserEntity;
import com.github.supercoding.repository.users.UserRepository;
import com.github.supercoding.service.exceptions.InvalidValueException;
import com.github.supercoding.service.exceptions.NotAcceptException;
import com.github.supercoding.service.exceptions.NotFoundException;
import com.github.supercoding.service.mapper.TicketMapper;
import com.github.supercoding.web.dto.airline.PaymentRequest;
import com.github.supercoding.web.dto.airline.ReservationRequest;
import com.github.supercoding.web.dto.airline.ReservationResult;
import com.github.supercoding.web.dto.airline.Ticket;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AirReservationService {
    private UserRepository userRepository;
    private AirlineTicketRepository airlineTicketRepository;
    private ReservationRepository reservationRepository;
    private PassengerRepository passengerRepository;
    private PaymentRepository paymentRepository;

    public AirReservationService(UserRepository userRepository, AirlineTicketRepository airlineTicketRepository, PassengerRepository passengerRepository, ReservationRepository reservationRepository, PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.airlineTicketRepository = airlineTicketRepository;
        this.passengerRepository = passengerRepository;
        this.reservationRepository = reservationRepository;
        this.paymentRepository = paymentRepository;
    }

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
        UserEntity userEntity = userRepository.getUserInfoById(userId ).orElseThrow(() ->
                new NotFoundException("해당 ID : "+userId+" 유저를 찾을 수 없습니다."));
        String likePlace = userEntity.getLikeTravelPlace();
        List<AirlineTicket> airlineTickets =
                airlineTicketRepository.getAllAirlineTicketsWithPlaceAndTicketType(likePlace, ticketType);
        if(airlineTickets.isEmpty()) { throw new NotFoundException("해당 likePlace : "+likePlace+ "와 TicketType : "+ticketType+" 에 해당하는 티켓을 찾을 수 없습니다."); }
        List<Ticket> tickets = airlineTickets.stream().map(TicketMapper.INSTANCE::airlineTicketToTicket).collect(Collectors.toList());
        return tickets;
    }
    @Transactional(transactionManager = "tm2")
    public ReservationResult makeReservation(ReservationRequest reservationRequest) {
        // 1. Reservation Repository, Join table(flight/airline_ticket), Passenger repository
        // 1. passenger
        Integer userId = reservationRequest.getUserId();
        Integer airlineTicketId = reservationRequest.getAirlineTicketId();
        Passenger passenger = passengerRepository.findPassengerByUserId(userId)
                .orElseThrow(() -> new NotFoundException("요청하신 userId: "+userId+ "에 해당하는 Passenger를 찾을 수 없습니다."));
        Integer passengerId = passenger.getPassengerId();

        //2. price 등의 정보 불러오기
        List<AirlineTicketAndFlightInfo> airlineTicketAndFlightInfo = airlineTicketRepository.getAllAirlineTicketsAndFlightInfo(airlineTicketId);
        if (!airlineTicketAndFlightInfo.isEmpty()) {
            throw new NotFoundException("AirlineTicket ID: "+ airlineTicketId+ "에 해당하는 항공편과 항공권을 찾을 수 없습니다.");
        }
        //3. reservation 생성
        Reservation reservation = new Reservation(passengerId,airlineTicketId);
        Boolean isSuccess = false;
        try {
            isSuccess = reservationRepository.saveReservation(reservation) > 0;
        } catch (RuntimeException e) {
            throw new NotAcceptException("Reservation이 등록되는 과정이 거부되었습니다.");
        }

        System.out.println(airlineTicketAndFlightInfo);
        //TODO: ReservationResult DTO 만들기
        List<Integer> prices = airlineTicketAndFlightInfo.stream().map(AirlineTicketAndFlightInfo::getPrice).collect(Collectors.toList());
        List<Integer> charges = airlineTicketAndFlightInfo.stream().map(AirlineTicketAndFlightInfo::getCharges).collect(Collectors.toList());
        Integer tax = airlineTicketAndFlightInfo.stream().map(AirlineTicketAndFlightInfo::getTax).findFirst().get();
        Integer totalPrice = airlineTicketAndFlightInfo.stream().map(AirlineTicketAndFlightInfo::getTotalPrice).findFirst().get();

        return new ReservationResult(prices,charges,tax,totalPrice,isSuccess);

    }
    @Transactional(transactionManager = "tm2")
    public String makePayment(PaymentRequest paymentRequest) {
        // userIds, ticketIds
        List<Integer> userIds = paymentRequest.getUserIds();
        List<Integer> airlineTicketIds = paymentRequest.getAirlineTicketIds();
        if(userIds == null || airlineTicketIds == null || userIds.size() != airlineTicketIds.size()){
            throw new RuntimeException("userId와 airlineTicketId 를 다시 확인해주세요");
        }
        //passengerIds
        List<Integer> passengerIds = userIds.stream().map(userId -> passengerRepository
                .findPassengerByUserId(userId).orElseThrow(()->new NotFoundException("유저: "+userId+"를 찾을 수 없습니다."))
                .getPassengerId()).collect(Collectors.toList());
        //prices : 가격의 흐름까지는 필요 없음
        List<List<AirlineTicketAndFlightInfo>> airlineTicketAndFlightInfos = airlineTicketIds.stream().map(ticketId -> airlineTicketRepository.getAllAirlineTicketsAndFlightInfo(ticketId)).collect(Collectors.toList());

        //reservation : passenger_id, airline_ticket_id
        List<Reservation> reservationList = new ArrayList<>();
        int paymentSuccessCnt = 0;

        for(int i =0; i < userIds.size() ; i++) {
            Integer passengerId = passengerIds.get(i);
            Integer airlineTicketId = airlineTicketIds.get(i);

            Reservation reservation = new Reservation(passengerId, airlineTicketId);
            Integer reservationId = reservationRepository.saveReservation(reservation);
            Reservation getReservation = reservationRepository.findReservationById(reservationId);
            reservationList.add(getReservation);

        }

        //payment : passenger_id, reserve_id
        for (Reservation reservation : reservationList) {
            if(reservation == null) continue;
            if(reservation.getReservationStatus() == "확정") continue;
            Payment payment = new Payment(reservation.getReservationId(),reservation.getPassengerId());
            Boolean isSuccess = paymentRepository.savePayment(payment);
            //reservation 대기 -> 확정
            if(isSuccess){
                paymentSuccessCnt++;
                reservationRepository.updateReservationStatus(reservation.getReservationId(),"확정");
            }
        }

        return "요청하신 결제 중" + paymentSuccessCnt+"건 진행 완료 되었습니다.";

    }
}
