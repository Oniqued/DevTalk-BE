package com.devtalk.payment.paymentservice.application.port.out.repository;

import com.devtalk.payment.paymentservice.domain.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentQueryableRepo{
    Optional<Payment> findByConsultationId(String consultationId);
}
