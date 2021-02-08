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

    private static final double ONE_HOUR_SECONDS = 3600.0;
    private final List<PricePlan> pricePlans;
    private final MeterReadingService meterReadingService;

    public PricePlanService(List<PricePlan> pricePlans, MeterReadingService meterReadingService) {
        this.pricePlans = pricePlans;
        this.meterReadingService = meterReadingService;
    }

    /**
     * For the given PlanName - get all the MeterReadings & calculate cost for all the PricePlans
     * return back - to compare and choose the best one
     *
     * One SmartMeterId - Many Readings > threadSafe; none of them change during method execution > can refactor calculateCost
     *
     * @param smartMeterId
     * @return
     */
    public Optional<Map<String, BigDecimal>> getConsumptionCostOfElectricityReadingsForEachPricePlan(String smartMeterId) {
        Optional<List<ElectricityReading>> electricityReadings = meterReadingService.getReadings(smartMeterId);

        if (!electricityReadings.isPresent()) {
            return Optional.empty();
        }

        final BigDecimal calculatedAvgReadingCost = calculateCost(electricityReadings.get());

        return Optional.of(pricePlans.stream().collect(
                Collectors.toMap(PricePlan::getPlanName, t -> calculatedAvgReadingCost.multiply(t.getUnitRate())
                )));

        //Refactored code to improve efficiency
        /*return Optional.of(pricePlans.stream().collect(
                Collectors.toMap(PricePlan::getPlanName, t -> calculateCost(electricityReadings.get(), t))));*/
    }


    /**
     * 1) calculateAverageReading = allReadings/countofReadings
     * 2) calculateTimeElapsed = (max-min Readings Time) / 1hr
     * 3) averagedCost/CostPerHour = avg/time > rounded half up > 1.5 to 2 & 1.1 to 1
     *
     * @param electricityReadings
     * @return
     */
    private BigDecimal calculateCost(List<ElectricityReading> electricityReadings) {
        BigDecimal average = calculateAverageReading(electricityReadings);
        BigDecimal timeElapsed = calculateTimeElapsed(electricityReadings);

        return average.divide(timeElapsed, RoundingMode.HALF_UP);
    }


    /**
     * 1) calculateAverageReading = allReadings/countofReadings
     * 2) calculateTimeElapsed = (max-min Readings Time) / 1hr
     * 3) averagedCost/CostPerHour = avg/time > rounded half up > 1.5 to 2 & 1.1 to 1
     *
     * 4) avg cost for the given plan = averagedCost * unitRate
     *
     * @param electricityReadings
     * @param pricePlan
     * @return
     */
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

        return BigDecimal.valueOf(Duration.between(first.getTime(), last.getTime()).getSeconds() / ONE_HOUR_SECONDS);
    }

}
