package org.RMQSales.repository;

import org.RMQSales.entity.Payments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface PaymentsRepository extends JpaRepository<Payments, UUID>, JpaSpecificationExecutor<Payments> {
}
