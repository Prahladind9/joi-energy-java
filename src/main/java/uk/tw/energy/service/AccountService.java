package uk.tw.energy.service;

import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Account-PricePlanName Maintenance service >
 * Map for better retrieving the
 * Independent DataStructure Implementation - No Entity used.
 *
 * Can be use for many Readings based scenario >> Electricity/Water
 */
@Service
public class AccountService {

    //todo: Check & Add Test Cases
    // 1) Case Sensitive Handling - Hope not required
    // 2) NullPointer Exception is not handled

    private final Map<String, String> smartMeterToPricePlanAccounts;

    public AccountService(Map<String, String> smartMeterToPricePlanAccounts) {
        this.smartMeterToPricePlanAccounts = smartMeterToPricePlanAccounts;
    }

    public String getPricePlanIdForSmartMeterId(String smartMeterId) {
        return smartMeterToPricePlanAccounts.get(smartMeterId);
    }
}
