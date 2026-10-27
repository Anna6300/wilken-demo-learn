package de.demo.energy.billing;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/** A meter reading (Zählerstand) taken on a given day. */
public record MeterReading(String meterId, LocalDate date, BigDecimal value) {

    public MeterReading {
        Objects.requireNonNull(meterId, "meterId");
        Objects.requireNonNull(date, "date");
        Objects.requireNonNull(value, "value");
        if (value.signum() < 0) {
            throw new IllegalArgumentException("meter value must not be negative");
        }
    }
}