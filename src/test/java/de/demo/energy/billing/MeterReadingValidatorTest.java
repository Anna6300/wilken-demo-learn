package de.demo.energy.billing;

import de.demo.energy.qa.Requirement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
@Requirement("MESS-201")
@DisplayName("Plausibilitätsprüfung Zählerstände")
class MeterReadingValidatorTest {

    private static final BigDecimal EXPECTED_PER_YEAR = new BigDecimal("3650"); // 10 kWh per day
    private static final MeterReading PREVIOUS = reading("2026-01-01", "10000");

    private final MeterReadingValidator validator = new MeterReadingValidator();

    @Test
    @Tag("smoke")
    @DisplayName("Normaler Verbrauch ist plausibel")
    void normalConsumptionIsOk() {
        assertThat(validator.validate(PREVIOUS, reading("2026-01-11", "10100"), EXPECTED_PER_YEAR))
                .isEqualTo(PlausibilityResult.OK);
    }

    @Test
    @DisplayName("Grenzwert: genau doppelter Erwartungswert ist noch plausibel")
    void exactlyTwiceExpectedIsStillOk() {
        assertThat(validator.validate(PREVIOUS, reading("2026-01-11", "10200"), EXPECTED_PER_YEAR))
                .isEqualTo(PlausibilityResult.OK);
    }

    @Test
    @DisplayName("Grenzwert überschritten: Klärfall zur manuellen Prüfung")
    void aboveTwiceExpectedGoesToManualReview() {
        assertThat(validator.validate(PREVIOUS, reading("2026-01-11", "10200.01"), EXPECTED_PER_YEAR))
                .isEqualTo(PlausibilityResult.MANUAL_REVIEW_HIGH_CONSUMPTION);
    }

    @Test
    @DisplayName("Zählerstand kleiner als Vorablesung wird abgelehnt")
    void lowerReadingIsRejected() {
        assertThat(validator.validate(PREVIOUS, reading("2026-01-11", "9999"), EXPECTED_PER_YEAR))
                .isEqualTo(PlausibilityResult.REJECTED_LOWER_THAN_PREVIOUS);
    }

    @Test
    @DisplayName("Ablesedatum am selben Tag wird abgelehnt")
    void sameDayIsRejected() {
        assertThat(validator.validate(PREVIOUS, reading("2026-01-01", "10001"), EXPECTED_PER_YEAR))
                .isEqualTo(PlausibilityResult.REJECTED_INVALID_DATE);
    }

    private static MeterReading reading(String date, String value) {
        return new MeterReading("1ESY1160000001", LocalDate.parse(date), new BigDecimal(value));
    }
}
