package de.demo.energy.billing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;

/**
 * Checks whether a new meter reading is plausible compared to the previous one.
 * Implausible readings must never be billed automatically.
 */
public class MeterReadingValidator {

    /** Consumption above this factor of the expected value goes to manual review. */
    static final BigDecimal HIGH_CONSUMPTION_FACTOR = new BigDecimal("2.0");

    public PlausibilityResult validate(MeterReading previous, MeterReading current, BigDecimal expectedKwhPerYear) {
        if (!current.date().isAfter(previous.date())) {
            return PlausibilityResult.REJECTED_INVALID_DATE;
        }
        BigDecimal consumption = current.value().subtract(previous.value());
        if (consumption.signum() < 0) {
            return PlausibilityResult.REJECTED_LOWER_THAN_PREVIOUS;
        }
        long days = ChronoUnit.DAYS.between(previous.date(), current.date());
        BigDecimal expectedForPeriod = expectedKwhPerYear
                .multiply(BigDecimal.valueOf(days))
                .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);
        if (consumption.compareTo(expectedForPeriod.multiply(HIGH_CONSUMPTION_FACTOR)) > 0) {
            return PlausibilityResult.MANUAL_REVIEW_HIGH_CONSUMPTION;
        }
        return PlausibilityResult.OK;
    }
}