package com.github.supercoding.repository.roles;

import jakarta.persistence.*;
import lombok.*;

import javax.swing.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Roles {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;
    @Column(name = "name" , nullable = false)
    private String name;
}
