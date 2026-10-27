package de.demo.energy.billing;

/** Outcome of a meter reading plausibility check (Plausibilitätsprüfung). */
public enum PlausibilityResult {
    /** Reading can be billed automatically. */
    OK,
    /** Reading is lower than the previous one - meter rollback or typo. */
    REJECTED_LOWER_THAN_PREVIOUS,
    /** Reading date is not after the previous reading. */
    REJECTED_INVALID_DATE,
    /** Consumption is far above the expected value - needs manual clearing (Klärfall). */
    MANUAL_REVIEW_HIGH_CONSUMPTION
}