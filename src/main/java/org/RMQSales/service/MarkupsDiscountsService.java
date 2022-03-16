package org.RMQSales.service;

import org.RMQSales.entity.MarkupsDiscounts;
import org.RMQSales.repository.MarkupsDiscountsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MarkupsDiscountsService {
    MarkupsDiscountsRepository markupsDiscountsRepository;

    @Autowired
    public void setMarkupsDiscountsRepository(MarkupsDiscountsRepository markupsDiscountsRepository) {
        this.markupsDiscountsRepository = markupsDiscountsRepository;
    }

    public void save(MarkupsDiscounts object) {
        markupsDiscountsRepository.save(object);
    }
}
