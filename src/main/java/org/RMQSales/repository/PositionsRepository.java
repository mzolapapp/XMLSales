package org.RMQSales.repository;

import org.RMQSales.entity.Positions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface PositionsRepository extends JpaRepository<Positions, UUID>, JpaSpecificationExecutor<Positions> {
    Positions findByCheckIdAndCheckLink(UUID check, int check_link);
}
