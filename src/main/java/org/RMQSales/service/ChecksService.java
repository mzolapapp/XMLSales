package org.RMQSales.service;

import org.RMQSales.entity.Checks;
import org.RMQSales.repository.CheckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChecksService {
    CheckRepository checkRepository;

    @Autowired
    public void setCheckRepository(CheckRepository checkRepository) {
        this.checkRepository = checkRepository;
    }

    public void save(Checks object) {
        checkRepository.save(object);
    }
}
