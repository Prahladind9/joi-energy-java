package uk.tw.energy.service;

import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PricePlanService {

    public static final double ONE_HOUR_IN_SECONDS = 3600.0;
    private final List<PricePlan> pricePlans;
    private final MeterReadingService meterReadingService;

    public PricePlanService(List<PricePlan> pricePlans, MeterReadingService meterReadingService) {
        this.pricePlans = pricePlans;
        this.meterReadingService = meterReadingService;
    }


    /**
     * 1) Last 7 days readings
     * 2) Calcualte the avg for all the readings
     * 3) Calcualte the cost for the given plan id
     *
     * @param smartMeterId
     * @param pricePlanId
     * @param days
     * @return
     */
    public Optional<BigDecimal> getConsumptionCostOfElectricityReadingsForPricePlanForGivenDays(String smartMeterId, String pricePlanId, int days) {
        Optional<List<ElectricityReading>> electricityReadings =
                meterReadingService.getReadingsForSmartMeterIdForDays(smartMeterId, days);
        System.out.println("electricityReadings>> "+ electricityReadings.get());
        if (!electricityReadings.isPresent()) {
            return Optional.empty();
        }

        final BigDecimal calculatedAvgReadingCost = calculatedAvgReadingCost(electricityReadings.get());
        System.out.println("calculatedAvgReadingCost>> "+ calculatedAvgReadingCost);

        return Optional.of(pricePlans.stream()
                .filter(t -> t.getPlanName().equals(pricePlanId))
                .map(t -> calculatedAvgReadingCost.multiply(t.getUnitRate()))
                .collect(Collectors.toList()).get(0));

    }

    public Optional<Map<String, BigDecimal>> getConsumptionCostOfElectricityReadingsForEachPricePlan(String smartMeterId) {
        Optional<List<ElectricityReading>> electricityReadings = meterReadingService.getReadingsForSmartMeterIdForDays(smartMeterId);

        if (!electricityReadings.isPresent()) {
            return Optional.empty();
        }

        final BigDecimal calculatedAvgReadingCost = calculatedAvgReadingCost(electricityReadings.get());

        return Optional.of(pricePlans.stream().collect(
                Collectors.toMap(PricePlan::getPlanName, t -> calculatedAvgReadingCost.multiply(t.getUnitRate())
                )));

        //Commented & improved the performance in the above steps
        /*return Optional.of(pricePlans.stream().collect(
                Collectors.toMap(PricePlan::getPlanName, t -> calculateCost(electricityReadings.get(), t))));*/
    }


    private BigDecimal calculatedAvgReadingCost(List<ElectricityReading> electricityReadings) {
        BigDecimal average = calculateAverageReading(electricityReadings);
        BigDecimal timeElapsed = calculateTimeElapsed(electricityReadings);

        return average.divide(timeElapsed, RoundingMode.HALF_UP);
    }


    private BigDecimal calculateCost(List<ElectricityReading> electricityReadings, PricePlan pricePlan) {
        BigDecimal average = calculateAverageReading(electricityReadings);
        BigDecimal timeElapsed = calculateTimeElapsed(electricityReadings);

        BigDecimal averagedCost = average.divide(timeElapsed, RoundingMode.HALF_UP);
        return averagedCost.multiply(pricePlan.getUnitRate());
    }

    private BigDecimal calculateAverageReading(List<ElectricityReading> electricityReadings) {
        BigDecimal summedReadings = electricityReadings.stream()
                .map(ElectricityReading::getReading)
                .reduce(BigDecimal.ZERO, (reading, accumulator) -> reading.add(accumulator));

        return summedReadings.divide(BigDecimal.valueOf(electricityReadings.size()), RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTimeElapsed(List<ElectricityReading> electricityReadings) {
        ElectricityReading first = electricityReadings.stream()
                .min(Comparator.comparing(ElectricityReading::getTime))
                .get();
        ElectricityReading last = electricityReadings.stream()
                .max(Comparator.comparing(ElectricityReading::getTime))
                .get();

        return BigDecimal.valueOf(Duration.between(first.getTime(), last.getTime()).getSeconds() / ONE_HOUR_IN_SECONDS);
    }

}
