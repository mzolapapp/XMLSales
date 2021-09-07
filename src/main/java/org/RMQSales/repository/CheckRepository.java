package org.RMQSales.repository;

import org.RMQSales.entity.Checks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface CheckRepository extends JpaRepository<Checks, UUID>, JpaSpecificationExecutor<Checks> {


}
