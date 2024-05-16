package tests;


import bankManagerFlow.Accounts;
import bankManagerFlow.BankManager;
import bankManagerFlow.Customer;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import propertyUtility.PropertyUtility;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class BankManagerTest {
    public WebDriver webDriver;

    @Test
    public void methodTest() {
        //webDriver
        webDriver = new ChromeDriver();
        webDriver.manage().window().maximize();
        webDriver.get("https://www.globalsqa.com/angularJs-protractor/BankingProject/#/login");

        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
        PropertyUtility propertyUtility = new PropertyUtility("AddCustomerDataA");

        BankManager bankManagerTT = new BankManager();
        Customer customer = new Customer();

        customer.setFirstName(propertyUtility.getAllData().get("firstNameValue"));
        customer.setLastName(propertyUtility.getAllData().get("lastNameValue"));
        customer.setPostCode(propertyUtility.getAllData().get("postCodeValue"));

        bankManagerTT.getCustomers().add(customer);

        //chemam datele din properties
        String firstNameValue = bankManagerTT.getCustomers().get(0).getFirstName();
        String lastNameValue = bankManagerTT.getCustomers().get(0).getLastName();
        String postCodeValue = bankManagerTT.getCustomers().get(0).getPostCode();

        //datele salvate pe parcurs
        List<String> requestedCurrencyList = List.of(propertyUtility.getAllData().get("accountCurrencies").split(","));
        List<String> accountsCurrencyCreated = new ArrayList<>();

        //accesam pagina bank manager
        WebElement bankManagerPage = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Bank Manager Login']")));
        bankManagerPage.click();

        //accesam pagina add customer
        WebElement addCustomer = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@ng-click='addCust()']")));
        addCustomer.click();

        //introducem datele si dam click
        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Add Customer']")));
        WebElement firstName = webDriver.findElement(By.cssSelector("input[placeholder='First Name'"));
        WebElement lastName = webDriver.findElement(By.cssSelector("input[placeholder='Last Name'"));
        WebElement postCode = webDriver.findElement(By.cssSelector("input[placeholder='Post Code'"));
        firstName.sendKeys(firstNameValue);
        lastName.sendKeys(lastNameValue);
        postCode.sendKeys(postCodeValue);
        addButton.click();

        //preluam informatia din alert si inchidem alerta
        Alert alert = webDriver.switchTo().alert();
        String alertText = alert.getText();
        int accountID = Integer.parseInt(alertText.split(":")[1].trim());
        //customer.setCustomerId(Integer.parseInt(alertText.split(":")[1].trim()));
        alert.accept();

        //accesam pagina open account
        WebElement openAccount = webDriver.findElement(By.xpath("//button[@ng-click='openAccount()']"));
        openAccount.click();

        //introducem datele preluate din propertie files si din alert anterioara
        WebElement currencyDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("currency")));
        WebElement pressProcess = webDriver.findElement(By.xpath("//button[text()='Process']"));
        List<WebElement> findUserByID = webDriver.findElements(By.xpath("//*[@id='userSelect']/option"));

        Select select = new Select(currencyDropdown);
        List<WebElement> selectCurrency = select.getOptions();

        //creem o functie care parcurge lista din properties si selecteaza valuta corespunzatoare cu cea de pe site
        for (String currency : requestedCurrencyList) {
            for (WebElement currencyElement : selectCurrency) {
                String currentCurrency = currencyElement.getText();
                if (currentCurrency.equals(currency)) {
                    findUserByID.get(accountID).click();
                    currencyElement.click();
                    pressProcess.click();
                    String alertTextB = alert.getText();
                    String accountCurrency = alertTextB.split(":")[1].trim();
                    accountsCurrencyCreated.add(accountCurrency);
                    alert.accept();
                }
            }
        }

//        Accounts account = new Accounts();
//        account.setCurrency(accountsCurrencyCreated);
//        account.setCustomerId(customer.getCustomerId());
//        bankManagerTT.getCustomers().get(0).getAccounts().add(account);

        //accesam pagina Customers
        WebElement customersList = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@ng-click='showCust()']")));
        customersList.click();

        //cautam userul creat dupa o functie si adaugam conturile create de le un user specific intr-o lista
        List<WebElement> firstNameList = webDriver.findElements(By.xpath("//table/tbody/tr/td[1]"));
        List<WebElement> lastNameList = webDriver.findElements(By.xpath("//table/tbody/tr/td[2]"));
        List<WebElement> postCodeList = webDriver.findElements(By.xpath("//table/tbody/tr/td[3]"));
        List<WebElement> accountsWebList = webDriver.findElements(By.xpath("//table/tbody/tr/td[4]"));
        List<WebElement> deleteList = webDriver.findElements(By.xpath("//table/tbody/tr/td[5]/button"));

        //verificam conturile create din lista si conturile online dupa care stergem userul
        for (int i = 0; i < lastNameList.size(); i++) {
            if (firstNameList.get(i).getText().trim().equals(firstNameValue)) {

                Assert.assertEquals(firstNameList.get(i).getText().trim(), firstNameValue);
                Assert.assertEquals(lastNameList.get(i).getText().trim(), lastNameValue);
                Assert.assertEquals(postCodeList.get(i).getText().trim(), postCodeValue);

                StringBuilder reformatAccountList = new StringBuilder();
                for (String accountC : accountsCurrencyCreated){
                    reformatAccountList.append(accountC).append(" ");
                }
                String result = reformatAccountList.toString().trim();
                Assert.assertEquals(accountsWebList.get(i).getText().trim(), result);

                deleteList.get(i).click();

            }
            System.out.println(deleteList.get(i).getText());
            System.out.println("lalala");
        }

        //inchide driverul
        //webDriver.quit();
    }
}
