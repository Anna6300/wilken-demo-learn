package de.demo.energy.billing.ui;

import de.demo.energy.qa.Requirement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end UI test for the customer portal, run with: mvn test -Dgroups=ui -Dexcluded.groups=
 *
 * <p>Target URL comes from -Dportal.url so the same test runs against a local build, the cloud
 * staging tenant, or an on-prem test installation.
 */
@Tag("ui")
@DisplayName("Kundenportal – Abschlagsrechner (Selenium)")
class InstalmentCalculatorUiTest {

    private WebDriver driver;
    private InstalmentCalculatorPage page;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1280,800");
        driver = new ChromeDriver(options);
        page = new InstalmentCalculatorPage(driver).open(portalUrl());
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Tag("smoke")
    @Requirement("BILL-102")
    @DisplayName("Kunde berechnet Abschlag für Standardhaushalt")
    void customerCalculatesInstalment() {
        page.selectTariff("basis").enterConsumption("3500").calculate();

        assertThat(page.result()).isEqualTo("Monatlicher Abschlag: 126 €");
    }

    @Test
    @Requirement("BILL-103")
    @DisplayName("Ungültiger Verbrauch zeigt Fehlermeldung")
    void invalidConsumptionShowsError() {
        page.enterConsumption("-5").calculate();

        assertThat(page.error()).isEqualTo("Bitte einen gültigen Verbrauch eingeben.");
        assertThat(page.result()).isEmpty();
    }

    private static String portalUrl() {
        return System.getProperty("portal.url",
                Path.of("src/main/resources/portal/abschlagsrechner.html").toAbsolutePath().toUri().toString());
    }
}
