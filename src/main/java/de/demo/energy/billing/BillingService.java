package de.demo.energy.billing;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calculates yearly energy costs and the monthly instalment (Abschlag).
 *
 * <p>Rules (simplified, see docs/QA-Strategy.md, requirement BILL-101..103):
 * <ul>
 *   <li>net = Grundpreis + consumption * Arbeitspreis</li>
 *   <li>gross = net * (1 + VAT), rounded to cents (HALF_UP)</li>
 *   <li>monthly instalment = gross / 12, rounded UP to full euros</li>
 * </ul>
 */
public class BillingService {

    public Invoice calculateYearlyInvoice(Tariff tariff, BigDecimal consumptionKwh) {
        if (consumptionKwh == null || consumptionKwh.signum() < 0) {
            throw new IllegalArgumentException("consumption must be >= 0");
        }
        BigDecimal net = tariff.basePricePerYear()
                .add(consumptionKwh.multiply(tariff.energyPricePerKwh()))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal vat = net.multiply(tariff.vatRate()).setScale(2, RoundingMode.HALF_UP);
        return new Invoice(net, vat, net.add(vat));
    }

    public BigDecimal monthlyInstalment(Tariff tariff, BigDecimal expectedKwhPerYear) {
        BigDecimal gross = calculateYearlyInvoice(tariff, expectedKwhPerYear).gross();
        return gross.divide(BigDecimal.valueOf(12), 0, RoundingMode.CEILING);
    }

    public record Invoice(BigDecimal net, BigDecimal vat, BigDecimal gross) {
    }
}