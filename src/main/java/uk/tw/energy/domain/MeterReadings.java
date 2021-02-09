package uk.tw.energy.domain;

import java.util.List;

/**
 * Currently used as Model
 */
public class MeterReadings<T> {

    private List<T> electricityReadings;
    private String smartMeterId;

    public MeterReadings() { }

    public MeterReadings(String smartMeterId, List<T> electricityReadings) {
        this.smartMeterId = smartMeterId;
        this.electricityReadings = electricityReadings;
    }

    public List<T> getElectricityReadings() {
        return electricityReadings;
    }

    public String getSmartMeterId() {
        return smartMeterId;
    }
}
