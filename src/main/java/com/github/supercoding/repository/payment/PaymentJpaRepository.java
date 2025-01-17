package com.github.supercoding.repository.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.relational.core.sql.In;

public interface PaymentJpaRepository extends JpaRepository<Payment, Integer> {
}
