package org.RMQSales.repository;

import org.RMQSales.entity.MarkupsDiscounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface MarkupsDiscountsRepository extends JpaRepository<MarkupsDiscounts, UUID>, JpaSpecificationExecutor<MarkupsDiscounts> {
}
