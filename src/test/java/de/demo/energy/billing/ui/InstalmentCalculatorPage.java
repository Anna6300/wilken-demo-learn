package de.demo.energy.billing.ui;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/** Page Object for the customer portal instalment calculator (Abschlagsrechner). */
public class InstalmentCalculatorPage {

    private static final By TARIFF = By.cssSelector("[data-testid='tariff']");
    private static final By CONSUMPTION = By.cssSelector("[data-testid='consumption']");
    private static final By CALCULATE = By.cssSelector("[data-testid='calculate']");
    private static final By RESULT = By.cssSelector("[data-testid='result']");
    private static final By ERROR = By.cssSelector("[data-testid='error']");

    private final WebDriver driver;

    public InstalmentCalculatorPage(WebDriver driver) {
        this.driver = driver;
    }

    public InstalmentCalculatorPage open(String url) {
        driver.get(url);
        return this;
    }

    public InstalmentCalculatorPage selectTariff(String value) {
        new Select(driver.findElement(TARIFF)).selectByValue(value);
        return this;
    }

    public InstalmentCalculatorPage enterConsumption(String kwh) {
        WebElement input = driver.findElement(CONSUMPTION);
        input.clear();
        input.sendKeys(kwh);
        return this;
    }

    public InstalmentCalculatorPage calculate() {
        driver.findElement(CALCULATE).click();
        return this;
    }

    public String result() {
        return driver.findElement(RESULT).getText();
    }

    public String error() {
        return driver.findElement(ERROR).getText();
    }
}
