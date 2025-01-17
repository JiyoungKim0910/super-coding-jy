package com.github.supercoding.repository.passenger;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PassengerJpaRepository extends JpaRepository<Passenger, Integer> {
    Optional<Passenger> findPassengerByUserUserId(Integer userId);
    List<Passenger> findAllByUserUserIdIn(List<Integer> userIds);
    Passenger findPassengersByPassengerId(Integer passengerId);

    //PassengerJdbcTemplateDao findPassengersByPassengerId(Integer passengerId);
}
