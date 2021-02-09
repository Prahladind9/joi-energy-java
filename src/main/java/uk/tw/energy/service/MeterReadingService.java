package uk.tw.energy.service;


import java.util.List;
import java.util.Optional;

public interface MeterReadingService<T> {
    Optional<List<T>> getReadings(String smartMeterId);
    void storeReadings(String smartMeterId, List<T> electricityReadings);
}
