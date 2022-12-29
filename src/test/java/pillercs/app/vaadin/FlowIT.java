package pillercs.app.vaadin;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;

public class FlowIT {

    @Test
    public void flowTest() throws InterruptedException {
        WebDriverManager.chromedriver().setup();

        ChromeDriver driver = new ChromeDriver();

        try {
            setupAndLogin(driver);

            createNewApplication(driver);

            searchAndChooseClient(driver);

            acceptDefaultLoanParameters(driver);

            recordEmployerAndIncome(driver);

            recordApplicantDetails(driver);

            underwritingResults(driver);

            selectOffers(driver);

            contract(driver);

            applicationApproved(driver);
        }  finally {
            driver.quit();
        }
    }

    private void clickAndSendKeys(WebElement input, String keys) {
        clickAndSendKeys(input, keys, null);
    }

    private void clickAndSendKeys(WebElement input, String keys, Keys additionalKey) {
        input.click();
        input.sendKeys(keys);
        if (additionalKey != null) input.sendKeys(additionalKey);
    }

    private void setupAndLogin(ChromeDriver driver) {
        driver.manage().window().maximize();

        //Loads the page
        driver.get("http://localhost:8080");

        //Have to explicitly wait because it takes time for compiled html to load
        WebElement userName = new WebDriverWait(driver, ofSeconds(5))
                .until(driver2 -> driver2.findElement(By.cssSelector("[name=\"username\"]")));

        userName.click();
        userName.sendKeys("user");

        WebElement password = driver.findElement(By.cssSelector("[name=\"password\"]"));
        password.click();
        password.sendKeys("pass");
        password.sendKeys(Keys.ENTER);
    }

    private void createNewApplication(ChromeDriver driver) {
        new WebDriverWait(driver, ofSeconds(30)).until(titleIs("Bank Höjd - Cash loan application"));

        WebElement createButton = driver.findElement(By.xpath("//vaadin-button[contains(.,'Create')]"));
        createButton.click();
    }

    private void searchAndChooseClient(ChromeDriver driver) {
        new WebDriverWait(driver, ofSeconds(30)).until(titleIs("Choosing the client"));

        WebElement searchButton = driver.findElement(By.xpath("//vaadin-button[contains(.,'Search')]"));
        searchButton.click();

        WebElement row = new WebDriverWait(driver, ofSeconds(3), ofSeconds(1))
                .until(_driver -> _driver.findElement(By.xpath("//vaadin-grid-cell-content[@slot='vaadin-grid-cell-content-12']")));
        row.click();

        WebElement selectButton = driver.findElement(By.xpath("//vaadin-button[contains(.,'Select')]"));
        new WebDriverWait(driver, ofSeconds(5), ofSeconds(1)).until(_driver -> selectButton.getAttribute("disabled") == null);
        selectButton.click();
    }

    private void acceptDefaultLoanParameters(ChromeDriver driver) {
        WebElement continueButton = new WebDriverWait(driver, ofSeconds(3), ofSeconds(1))
                .until(_driver -> _driver.findElement(By.xpath("//vaadin-button[contains(.,'Continue')]")));
        continueButton.click();
    }

    private void recordEmployerAndIncome(ChromeDriver driver) throws InterruptedException {
        WebElement addNewEmployer = new WebDriverWait(driver, ofSeconds(3), ofSeconds(1))
                .until(_driver -> _driver.findElement(By.xpath("//vaadin-button[contains(.,'new employer')]")));
        addNewEmployer.click();

        new WebDriverWait(driver, ofSeconds(2)).until(_driver -> visibilityOfAllElementsLocatedBy(By.xpath("//input")));
        Thread.sleep(1000);
        List<WebElement> inputs = driver.findElements(By.xpath("//input"));

        inputs.get(0).click();
        inputs.get(0).sendKeys("Test Company");

        //Tax number
        clickAndSendKeys(inputs.get(1), "1111-2222");

        //employment type
        clickAndSendKeys(inputs.get(2), "permanent", Keys.TAB);

        //Employment start date
        clickAndSendKeys(inputs.get(3), "01/01/2020", Keys.ENTER);

        WebElement addEmployerButton = driver.findElement(By.xpath("//vaadin-button[contains(.,'Add employer')]"));
        new WebDriverWait(driver, ofSeconds(5), ofSeconds(1)).until(_driver -> addEmployerButton.getAttribute("disabled") == null);
        addEmployerButton.click();

        WebElement continueToIncomesButton = driver.findElement(By.xpath("//vaadin-button[contains(.,'Continue to')]"));
        continueToIncomesButton.click();
        WebElement addIncomeButton = new WebDriverWait(driver, ofSeconds(5))
                .until(_driver -> _driver.findElement(By.xpath("//vaadin-button[contains(.,'Add income')]")));

        inputs = driver.findElements(By.xpath("//input"));
        inputs.forEach(i -> System.out.println("test ::: " + i.getTagName() + " " + i.getLocation()));

        //income type
        clickAndSendKeys(inputs.get(5), "pension", Keys.TAB);

        //income amount
        clickAndSendKeys(inputs.get(7), "1000000", Keys.TAB);

        new WebDriverWait(driver, ofSeconds(5), ofSeconds(1)).until(_driver -> addIncomeButton.getAttribute("disabled") == null);
        addIncomeButton.click();

        driver.findElement(By.xpath("//vaadin-button[contains(.,'Next')]")).click();
    }

    private void recordApplicantDetails(ChromeDriver driver) {
        new WebDriverWait(driver, ofSeconds(5)).until(titleIs("Applicant details"));

        List<WebElement> inputs = driver.findElements(By.xpath("//input"));
        clickAndSendKeys(inputs.get(0), "primary", Keys.TAB);
        clickAndSendKeys(inputs.get(1), "single", Keys.TAB);
        clickAndSendKeys(inputs.get(2), "1");
        clickAndSendKeys(inputs.get(3), "0");
        clickAndSendKeys(inputs.get(4), "5500", Keys.TAB);

        WebElement nextButton = driver.findElement(By.xpath("//vaadin-button[contains(.,'Next')]"));
        nextButton.click();
    }

    private void underwritingResults(ChromeDriver driver) {
        new WebDriverWait(driver, ofSeconds(10), ofSeconds(2)).until(titleIs("Underwriting results"));
        driver.findElement(By.xpath("//vaadin-button[contains(.,'Next')]")).click();
    }

    private void selectOffers(ChromeDriver driver) {
        new WebDriverWait(driver, ofSeconds(10), ofSeconds(2)).until(titleIs("Offers"));
        WebElement offer = driver.findElement(By.className("offer")/*xpath("//div[@class=offer]")*/);
        offer.click();
        WebElement selectOfferButton = driver.findElement(By.xpath("//vaadin-button[contains(.,'Select')]"));
        new WebDriverWait(driver, ofSeconds(2))
                .until(_driver -> selectOfferButton.getAttribute("disabled") == null);
        selectOfferButton.click();
    }

    private void contract(ChromeDriver driver) {
        new WebDriverWait(driver, ofSeconds(10), ofSeconds(2)).until(titleIs("Contract"));
        driver.findElement(By.xpath("//vaadin-button[contains(.,'Contract signed')]")).click();
    }

    private void applicationApproved(ChromeDriver driver) {
        new WebDriverWait(driver, ofSeconds(5)).until(titleIs("Application approved"));

        assertEquals("http://localhost:8080/application-approved", driver.getCurrentUrl());
    }
}
