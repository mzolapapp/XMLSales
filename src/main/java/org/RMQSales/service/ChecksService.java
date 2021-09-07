package org.RMQSales.service;

import org.RMQSales.entity.Checks;

import java.util.UUID;

public interface ChecksService {

    void save(Checks object);

    Checks getOne(UUID id);
}
