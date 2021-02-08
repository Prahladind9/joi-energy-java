package uk.tw.energy.service;

import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Independent MeterReadingService > logic similar to MeterReadings Entity
 * > but the entity is not directly implemented
 *
 * For quick retrieving Map is used instead of List<MeterReadings> meterAssociatedReadings
 *
 * ElectricityReading > can use generics and abstract out the methods into another class
 * Can reuse the contract with any other readings-scenarios
 *
 * WaterReading or any other
 *
 */
@Service
public class MeterReadingService {

    private final Map<String, List<ElectricityReading>> meterAssociatedReadings;

    public MeterReadingService(Map<String, List<ElectricityReading>> meterAssociatedReadings) {
        this.meterAssociatedReadings = meterAssociatedReadings;
    }

    /**
     * Get ElectricityReading for given MeterId
     * @param smartMeterId
     * @return
     */
    public Optional<List<ElectricityReading>> getReadings(String smartMeterId) {
        return Optional.ofNullable(meterAssociatedReadings.get(smartMeterId));
    }

    /**
     * Store ElectricityReading against the given meterId
     * @param smartMeterId
     * @param electricityReadings
     */
    public void storeReadings(String smartMeterId, List<ElectricityReading> electricityReadings) {
        if (!meterAssociatedReadings.containsKey(smartMeterId)) {
            meterAssociatedReadings.put(smartMeterId, new ArrayList<>());
        }
        meterAssociatedReadings.get(smartMeterId).addAll(electricityReadings);
    }
}
