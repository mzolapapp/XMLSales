package org.RMQSales.service;

import org.RMQSales.entity.Checks;
import org.RMQSales.entity.Positions;

public interface PositionsService {
    void save(Positions object);

    Positions getByCheckIdAndCheckLink(Checks check, int check_link);
}
