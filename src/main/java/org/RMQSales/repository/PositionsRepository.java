package org.RMQSales.repository;

import org.RMQSales.entity.Checks;
import org.RMQSales.entity.Positions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface PositionsRepository extends JpaRepository<Positions, UUID>, JpaSpecificationExecutor<Positions> {
    Positions findFirstByCheckIdAndCheckLink(Checks check, int check_link);
}
