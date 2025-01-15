package com.github.supercoding.repository.users;


import com.github.supercoding.repository.passenger.Passenger;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "userId")
@ToString
@Entity
@Table(name = "users")
public class UserEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;
    @Column(name = "user_name", length = 20)
    private String userName;
    @Column(name = "like_travel_place", length = 30)
    private String likeTravelPlace;
    @Column(name = "phone_num", length = 30)
    private String phoneNumber;

    //양방향으로 임의 설정
    @OneToOne(mappedBy = "user") //FK에 설정된 이름으로 매핑
    private Passenger passenger;

}
