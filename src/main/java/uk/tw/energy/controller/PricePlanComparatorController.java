package uk.tw.energy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.tw.energy.service.AccountService;
import uk.tw.energy.service.PricePlanService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/price-plans")
public class PricePlanComparatorController {

    public final static String PRICE_PLAN_ID_KEY = "pricePlanId";
    public final static String PRICE_PLAN_COMPARISONS_KEY = "pricePlanComparisons";
    private final PricePlanService pricePlanService;
    private final AccountService accountService;

    public PricePlanComparatorController(PricePlanService pricePlanService, AccountService accountService) {
        this.pricePlanService = pricePlanService;
        this.accountService = accountService;
    }

    @GetMapping("/compare-all/{smartMeterId}")
    public ResponseEntity<Map<String, Object>> calculatedCostForEachPricePlan(@PathVariable String smartMeterId) {
        String pricePlanId = accountService.getPricePlanIdForSmartMeterId(smartMeterId);
        Optional<Map<String, BigDecimal>> consumptionsForPricePlans = getStringBigDecimalMap(smartMeterId);

        if (validateConsumptionsForPricePlans(consumptionsForPricePlans)) return ResponseEntity.notFound().build();

        Map<String, Object> pricePlanComparisons = new HashMap<>();
        pricePlanComparisons.put(PRICE_PLAN_ID_KEY, pricePlanId);
        pricePlanComparisons.put(PRICE_PLAN_COMPARISONS_KEY, consumptionsForPricePlans.get());

        return ResponseEntity.ok(pricePlanComparisons);

        //Redundant below condition
        /*return consumptionsForPricePlans.isPresent()
                ? ResponseEntity.ok(pricePlanComparisons)
                : ResponseEntity.notFound().build();*/

    }

    @GetMapping("/recommend/{smartMeterId}")
    public ResponseEntity<List<Map.Entry<String, BigDecimal>>> recommendCheapestPricePlans(@PathVariable String smartMeterId,
                                                                                           @RequestParam(value = "limit", required = false) Integer limit) {
        Optional<Map<String, BigDecimal>> consumptionsForPricePlans = getStringBigDecimalMap(smartMeterId);

        if (validateConsumptionsForPricePlans(consumptionsForPricePlans)) return ResponseEntity.notFound().build();

        List<Map.Entry<String, BigDecimal>> recommendations = new ArrayList<>(consumptionsForPricePlans.get().entrySet());
        recommendations.sort(Comparator.comparing(Map.Entry::getValue));

        if (limit != null && limit < recommendations.size()) {
            recommendations = recommendations.subList(0, limit);
        }

        return ResponseEntity.ok(recommendations);
    }


    //refactored - assuming in future we can add more validations
    private boolean validateConsumptionsForPricePlans(Optional<Map<String, BigDecimal>> consumptionsForPricePlans) {
        if (!consumptionsForPricePlans.isPresent()) {
            return true;
        }
        return false;
    }

    private Optional<Map<String, BigDecimal>> getStringBigDecimalMap(String smartMeterId) {
        return pricePlanService.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeterId);
    }



    /*
    Consumer being UI; implementation before

    Javascript:

    function printName(name) {
      var ra = {
        "price-plan-2": 13.824,
        "price-plan-1": 27.648,
        "price-plan-0": 138.24
      };

      var myPlan = {"pricePlanId": "price-plan-0"};

      console.log(ra);

      console.log(Object.keys(ra));
      console.log(Object.values(ra));

      console.log('My Plan');
      console.log(Object.keys(ra).find(
          k => k == myPlan.pricePlanId
      ));

    }


    after structured:
    function printName(name) {
      var ra =
      {
          "plans" : [
            {"planName": "price-plan-2", "price": 13.824, "myPlan": false},
            {"planName": "price-plan-1", "price": 27.648, "myPlan": false},
            {"planName": "price-plan-0", "price": 138.24, "myPlan": true}
          ]
      }

      console.log(ra);


      console.log(ra.plans.find(
          p => p.myPlan === true
      ));

    }
     */

}
