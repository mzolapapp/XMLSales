package org.RMQSales.service;

import org.RMQSales.entity.Checks;
import org.RMQSales.entity.Positions;

import java.util.List;

public interface PositionsService {
    void save(Positions object);

    List<Positions> getByCheckIdAndCheckLink(Checks check, int check_link);
}
