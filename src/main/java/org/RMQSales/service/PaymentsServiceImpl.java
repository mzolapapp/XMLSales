package org.RMQSales.service;

import org.RMQSales.entity.Payments;
import org.RMQSales.repository.PaymentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentsServiceImpl implements PaymentsService{
    PaymentsRepository paymentsRepository;
    @Autowired
    public void setPaymentsRepository(PaymentsRepository paymentsRepository){
        this.paymentsRepository = paymentsRepository;
    }
    @Override
    public void save(Payments object) {
        paymentsRepository.save(object);
    }
}
