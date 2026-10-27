package de.demo.energy.billing;

import de.demo.energy.qa.Requirement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("unit")
@DisplayName("BillingService – Jahresrechnung und Abschlag")
class BillingServiceTest {

    private static final Tariff BASIS = new Tariff("Strom Basis",
            new BigDecimal("150.00"), new BigDecimal("0.32"), new BigDecimal("0.19"));

    private final BillingService billing = new BillingService();

    @Test
    @Tag("smoke")
    @Requirement("BILL-101")
    @DisplayName("Jahresrechnung: Netto, MwSt und Brutto für Standardhaushalt (3.500 kWh)")
    void yearlyInvoiceForStandardHousehold() {
        BillingService.Invoice invoice = billing.calculateYearlyInvoice(BASIS, new BigDecimal("3500"));

        assertThat(invoice.net()).isEqualByComparingTo("1270.00");
        assertThat(invoice.vat()).isEqualByComparingTo("241.30");
        assertThat(invoice.gross()).isEqualByComparingTo("1511.30");
    }

    @Test
    @Requirement("BILL-101")
    @DisplayName("Kein Verbrauch: nur der Grundpreis wird berechnet")
    void zeroConsumptionChargesOnlyBasePrice() {
        BillingService.Invoice invoice = billing.calculateYearlyInvoice(BASIS, BigDecimal.ZERO);

        assertThat(invoice.gross()).isEqualByComparingTo("178.50");
    }

    @ParameterizedTest(name = "{0} kWh -> {1} € pro Monat")
    @Requirement("BILL-102")
    @DisplayName("Abschlag wird auf volle Euro aufgerundet")
    @CsvSource({
            "0,      15",   //  178.50 / 12 =  14.875
            "1500,   63",   //  749.70 / 12 =  62.475
            "3500,  126",   // 1511.30 / 12 = 125.94
            "10000, 333"    // 3986.50 / 12 = 332.21
    })
    void monthlyInstalmentIsRoundedUp(String kwh, String expectedEuro) {
        assertThat(billing.monthlyInstalment(BASIS, new BigDecimal(kwh))).isEqualByComparingTo(expectedEuro);
    }

    @Test
    @Requirement("BILL-103")
    @DisplayName("Negativer Verbrauch wird abgelehnt")
    void negativeConsumptionIsRejected() {
        assertThatThrownBy(() -> billing.calculateYearlyInvoice(BASIS, new BigDecimal("-1")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Requirement("BILL-103")
    @DisplayName("Tarif mit negativem Preis kann nicht angelegt werden")
    void tariffWithNegativePriceIsRejected() {
        assertThatThrownBy(() -> new Tariff("Fehler", new BigDecimal("-1"), BigDecimal.ONE, BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("basePricePerYear");
    }
}