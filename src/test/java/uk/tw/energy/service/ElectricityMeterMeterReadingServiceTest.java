package uk.tw.energy.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.tw.energy.generator.ElectricityReadingsGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.tw.energy.Constants.RANDOM_METER_ID;

public class ElectricityMeterMeterReadingServiceTest {


    private ElectricityMeterMeterReadingService electricityMeterReadingService;

    @BeforeEach
    public void setUp() {
        electricityMeterReadingService = new ElectricityMeterMeterReadingService(new HashMap<>());
    }

    @Test
    public void givenMeterIdThatDoesNotExistShouldReturnNull() {
        assertThat(electricityMeterReadingService.getReadings("unknown-id")).isEqualTo(Optional.empty());
    }

    @Test
    public void givenMeterIdThatExistsShouldReturnMeterReadings() {
        electricityMeterReadingService.storeReadings(RANDOM_METER_ID, new ArrayList<>());
        assertThat(electricityMeterReadingService.getReadings(RANDOM_METER_ID)).isEqualTo(Optional.of(new ArrayList<>()));
    }

    @Test
    public void givenMeterIdThatExistsXRecordsShouldReturnMeterReadingsOfSizeX(){
        final int recordsSize = 2;
        electricityMeterReadingService.storeReadings(RANDOM_METER_ID, new ElectricityReadingsGenerator().generate(recordsSize));
        assertThat(electricityMeterReadingService.getReadings(RANDOM_METER_ID).get().size()).isEqualTo(recordsSize);
    }

}
