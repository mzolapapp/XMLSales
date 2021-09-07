package org.RMQSales.service;

import org.RMQSales.entity.Checks;
import org.RMQSales.repository.CheckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ChecksServiceImpl implements ChecksService {
    CheckRepository checkRepository;
    @Autowired
    public void setCheckRepository(CheckRepository checkRepository){
        this.checkRepository = checkRepository;
    }
    @Override
    public void save(Checks object) {
        checkRepository.save(object);
    }

    @Override
    public Checks getOne(UUID id) {
        return checkRepository.findById(id).get();
    }
}
