package uk.tw.energy.domain;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Aggregation > Independent Entity used to store electricity reading against an meter,
 * thus used in MeterReadings, MeterReadingService
 */
public class ElectricityReading {

    private Instant time;
    private BigDecimal reading; // kW

    public ElectricityReading() { }

    /**
     * Electricity Reading Definition
     *
     * @param time The date/time (as epoch) when the reading is taken
     * @param reading The consumption in kW at the time of the reading
     */
    public ElectricityReading(Instant time, BigDecimal reading) {
        this.time = time;
        this.reading = reading;
    }

    public BigDecimal getReading() {
        return reading;
    }

    public Instant getTime() {
        return time;
    }
}
