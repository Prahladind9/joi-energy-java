package uk.tw.energy.service;

import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MeterReadingService {

    private final Map<String, List<ElectricityReading>> meterAssociatedReadings;

    public MeterReadingService(Map<String, List<ElectricityReading>> meterAssociatedReadings) {
        this.meterAssociatedReadings = meterAssociatedReadings;
    }

    public Optional<List<ElectricityReading>> getReadingsForSmartMeterIdForDays(String smartMeterId) {
        return Optional.ofNullable(meterAssociatedReadings.get(smartMeterId));
    }

    public Optional<List<ElectricityReading>> getReadingsForSmartMeterIdForDays(String smartMeterId, int days) {
        return Optional.ofNullable(meterAssociatedReadings
                .get(smartMeterId).stream()
                .filter(t -> t.getTime().isAfter(getInstantBeforeGivenDays(days)))
                .collect(Collectors.toList())
        );
    }

    public Instant getInstantBeforeGivenDays(int days){
//        System.out.println("> 7Days >LocalDateTime> " + LocalDateTime.now().minusDays(days));
//        System.out.println("> 7Days >LocalDateTimeInstant>" + LocalDateTime.now().minusDays(days).atZone(ZoneId.systemDefault()).toInstant());
        return LocalDateTime.now().minusDays(days).atZone(ZoneId.systemDefault()).toInstant();
    }

    public Optional<List<ElectricityReading>> getReadings(String smartMeterId) {
        return Optional.ofNullable(meterAssociatedReadings.get(smartMeterId));
    }

    public void storeReadings(String smartMeterId, List<ElectricityReading> electricityReadings) {
        if (!meterAssociatedReadings.containsKey(smartMeterId)) {
            meterAssociatedReadings.put(smartMeterId, new ArrayList<>());
        }
        meterAssociatedReadings.get(smartMeterId).addAll(electricityReadings);
    }
}
