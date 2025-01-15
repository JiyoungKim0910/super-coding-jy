package com.github.supercoding.repository.passenger;

import com.github.supercoding.repository.users.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.apache.catalina.User;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "passengerId")
@Builder
@Entity
@Table(name = "passenger")
public class Passenger {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "passenger_id")
    private Integer passengerId;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private UserEntity user;
    @Column(name = "passport_num", length = 50)
    private String passportNum;


}
