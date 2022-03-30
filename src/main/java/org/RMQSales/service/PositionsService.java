package org.RMQSales.service;

import org.RMQSales.entity.Checks;
import org.RMQSales.entity.Positions;
import org.RMQSales.repository.PositionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PositionsService {
    PositionsRepository positionsRepository;

    @Autowired
    public void setPositionsRepository(PositionsRepository positionsRepository) {
        this.positionsRepository = positionsRepository;
    }

    public void save(Positions object) {
        positionsRepository.save(object);
    }


    public Positions getByCheckIdAndCheckLink(UUID check_id, int check_link) {
        return positionsRepository.findByCheckIdAndCheckLink(check_id, check_link);
    }


}
