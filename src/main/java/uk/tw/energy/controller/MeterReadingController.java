package uk.tw.energy.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.service.ElectricityMeterMeterReadingService;
import uk.tw.energy.service.MeterReadingService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/readings")
public class MeterReadingController<IN extends MeterReadings, OUT> {

    private final MeterReadingService meterReadingService;

    public MeterReadingController(ElectricityMeterMeterReadingService electricityMeterReadingService) {
        this.meterReadingService = electricityMeterReadingService;
    }

    /**
     * 1) Check meterReadings is valid
     * 2) Store/persist meterReadings in the meterReaderService
     * @param meterReadings MeterReadings Model Data Representation
     * @return
     */
    @PostMapping("/store")
    public ResponseEntity storeReadings(@RequestBody IN meterReadings) {
        if (!isMeterReadingsValid(meterReadings)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        meterReadingService.storeReadings(meterReadings.getSmartMeterId(), meterReadings.getElectricityReadings());
        return ResponseEntity.ok().build();
    }

    private boolean isMeterReadingsValid(IN meterReadings) {
        String smartMeterId = meterReadings.getSmartMeterId();
        List<OUT> electricityReadings = meterReadings.getElectricityReadings();
        return smartMeterId != null && !smartMeterId.isEmpty()
                && electricityReadings != null && !electricityReadings.isEmpty();
    }

    @GetMapping("/read/{smartMeterId}")
    public ResponseEntity readReadings(@PathVariable String smartMeterId) {
        Optional<List<OUT>> readings = meterReadingService.getReadings(smartMeterId);
        return readings.isPresent()
                ? ResponseEntity.ok(readings.get())
                : ResponseEntity.notFound().build();
    }
}
