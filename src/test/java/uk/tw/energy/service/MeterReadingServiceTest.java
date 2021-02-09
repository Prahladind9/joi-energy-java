package uk.tw.energy.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.tw.energy.generator.ElectricityReadingsGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.tw.energy.Constants.RANDOM_METER_ID;

public class MeterReadingServiceTest {

    private final int RECORD_SIZE = 2;
    private MeterReadingService meterReadingService;

    @BeforeEach
    public void setUp() {
        meterReadingService = new MeterReadingService(new HashMap<>());
    }

    @Test
    public void givenMeterIdThatDoesNotExistShouldReturnNull() {
        assertThat(meterReadingService.getReadings("unknown-id")).isEqualTo(Optional.empty());
    }

    @Test
    public void givenMeterIdThatExistsShouldReturnMeterReadings() {
        meterReadingService.storeReadings(RANDOM_METER_ID, new ArrayList<>());
        assertThat(meterReadingService.getReadings(RANDOM_METER_ID)).isEqualTo(Optional.of(new ArrayList<>()));
    }

    @Test
    public void givenMeterIdThatExistsXRecordsShouldReturnMeterReadingsOfSizeX(){
        meterReadingService.storeReadings(RANDOM_METER_ID, new ElectricityReadingsGenerator().generate(RECORD_SIZE));
        assertThat(meterReadingService.getReadings(RANDOM_METER_ID).get().size()).isEqualTo(RECORD_SIZE);
    }

}
