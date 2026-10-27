package de.demo.energy.billing;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Simplified electricity/gas tariff as used in a utility billing system.
 *
 * @param name               tariff name, e.g. "Strom Basis"
 * @param basePricePerYear   Grundpreis in EUR per year (net)
 * @param energyPricePerKwh  Arbeitspreis in EUR per kWh (net)
 * @param vatRate            VAT rate, e.g. 0.19
 */
public record Tariff(String name, BigDecimal basePricePerYear, BigDecimal energyPricePerKwh, BigDecimal vatRate) {

    public Tariff {
        Objects.requireNonNull(name, "name");
        requireNonNegative(basePricePerYear, "basePricePerYear");
        requireNonNegative(energyPricePerKwh, "energyPricePerKwh");
        requireNonNegative(vatRate, "vatRate");
    }

    private static void requireNonNegative(BigDecimal value, String field) {
        Objects.requireNonNull(value, field);
        if (value.signum() < 0) {
            throw new IllegalArgumentException(field + " must not be negative");
        }
    }
}