package com.xe.currencyconversion;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.FluentWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;


public class TestClass {

	//Using FindBy for locating elements
	@FindBy(how = How.XPATH, using = "//*[contains(text(), 'From')]/parent::div")  
	static WebElement currencyFromDropdown;
	
	@FindBy(how = How.XPATH, using = "//*[contains(text(), 'To')]/parent::div")
	static WebElement currencyToDropdown;
	
	@FindBy(how = How.ID, using = "amount")
	static WebElement amountField;
	
	@FindBy(how = How.XPATH, using = "//button[contains(@class, 'submit')]")
	static WebElement submitButton;
	
	@FindBy(how = How.XPATH, using = "//span[contains(@class, 'converterresult-unitConversion')][2]")
	static WebElement conversionrate;
	
	@FindBy(how = How.XPATH, using = "//input[@name='Amount']")
	static WebElement value;
	
	@FindBy(how = How.XPATH, using = "//div[@class='converterresult-conversionTo']/span[contains(@class,'toAmount')]")
	static WebElement actualConversionRate;
	
	
	public static WebDriver driver;
	public static FluentWait<WebDriver> wait;
	
	/**
	 * this testng beforemethod is to setup WebDriver of a particular browser and launch application before test method executes
	 * @param browserName
	 */
	@BeforeMethod
	@Parameters({ "browserName"})
	public void setup(String browserName) {
		
		        DesiredCapabilities cap = new DesiredCapabilities();
		  		  						
				if (browserName.equalsIgnoreCase("firefox")) {
					cap.setCapability(CapabilityType.BROWSER_NAME, "firefox");
					cap.setCapability(CapabilityType.SUPPORTS_APPLICATION_CACHE, false);
					cap.setCapability(CapabilityType.SUPPORTS_WEB_STORAGE , false);
					System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir") + "//browserDrivers//geckodriver.exe");
					driver = new FirefoxDriver(cap);

						
				}

				else if (browserName.equalsIgnoreCase("chrome")) {
					cap.setCapability(CapabilityType.BROWSER_NAME, "chrome");
					cap.setCapability(CapabilityType.SUPPORTS_APPLICATION_CACHE, false);
					cap.setCapability(CapabilityType.SUPPORTS_WEB_STORAGE , false);
					System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "//browserDrivers//chromedriver.exe");
					driver = new ChromeDriver(cap);

					
				}
				
				else if (browserName.equalsIgnoreCase("IE")) {
					
					cap.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
					System.setProperty("webdriver.ie.driver", System.getProperty("user.dir") + "//browserDrivers//IEDriverServer.exe");
					driver = new InternetExplorerDriver(cap);
					

				}
				
				//maximize browser window
				driver.manage().window().maximize();
				
				//launch xe.com in the browser
				driver.get("http://xe.com/");
				Set<Cookie> cookies = driver.manage().getCookies();
				System.out.println("count of cookies: " +cookies.size());
				driver.manage().deleteAllCookies();

				driver.manage().timeouts().implicitlyWait(70, TimeUnit.SECONDS);


			}
	

	/**
	 * This is test method for testing currency exchange with different set of test data
	 * @param n
	 */

	  @Test(dataProvider = "data")
	  public static void f(double currencyValue) {
		  
          //using PageFactory class to initialize page object elements
		  PageFactory.initElements(driver, TestClass.class);
		  
		  //find currency_from_field and select a currency
		  //implicit wait to wait until element is loaded on the page
		  driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		  Actions action = new Actions(driver);
		  action.moveToElement(currencyFromDropdown).click().build().perform();
		  driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		  action.moveToElement(driver.findElement(By.xpath("//*[contains(text(), 'EUR')]"))).click().build().perform();
		  
		  String getcurrencyFromValue = driver.findElement(By.xpath("//div/label[@for='from']//..//div/span[@class='dropdown-currencyCode']")).getText();
		  System.out.println("currency value 1: " +getcurrencyFromValue);
		  
		  //find currency_to_field and select a currency  		  
		  action.moveToElement(currencyToDropdown).click().build().perform();
	  
		  action.moveToElement(driver.findElement(By.xpath("//*[contains(text(), 'GBP')]"))).click().build().perform();
		  
		  String getcurrencyToValue = driver.findElement(By.xpath("//div/label[@for='to']//..//div/span[@class='dropdown-currencyCode']")).getText();
		  System.out.println("currency value 2: " +getcurrencyToValue);
		  
	      //enter value for currency exchange
		  amountField.sendKeys(String.valueOf(currencyValue));
		  
		  submitButton.click();
		  
		  System.out.println("current conversion rate: " +actualConversionRate.getText());
		  
		  //get the title of the page to display the currency exchange between 2 types of currecies
		  String expectedTitle = "XE Currency Converter: EUR to GBP";
		  String actualTitle = "XE Currency Converter: " +getcurrencyFromValue+ " to " +getcurrencyToValue;
		  Assert.assertEquals(actualTitle, expectedTitle, "currency exchange between 2 currencies are displayed");
		  
		  //check final result after conversion 
		  String conversionvalue = conversionrate.getText();
		  String conversionratesplit = conversionvalue.split(" ")[3];
		  System.out.println("expected value1: "+conversionratesplit);
		  
		  double enteredValue = Double.valueOf(value.getAttribute("value"));
		  System.out.println("entered value1: "+enteredValue);
		  
		  double expectedvalue = enteredValue * Double.valueOf(conversionratesplit);
		  System.out.println("expected value: "+expectedvalue);
		  
		  double finalvalue = Math.round(expectedvalue*100.0)/100.0;
		  System.out.println("rounded value: "+finalvalue);
		  
		  String actualvalue = actualConversionRate.getText();
		  String getactualvalueDouble = actualvalue.toString().replaceAll(",", "");
		  double finalresult = Double.valueOf(getactualvalueDouble);
		  double actualroundedValue = Math.round(finalresult*100.0)/100.0;
		  System.out.println("actual result after rounding: " +actualroundedValue);

		  //verifying if final result displayed is as expected
		  Assert.assertEquals(actualroundedValue, finalvalue);
				
		  System.out.println("----- Test Completed ------------");

	  }
	   
	  
	  /**
	   * this is DataProvider method and returns object arrays for using parameters for test data 
	   * @return
	   */
	  
	  @DataProvider (name = "data")
	  public static Object[][] values() {
			 Object [][] data = new Object [5][1];
			 
			 data [0][0] = 125.24;
			 data [1][0] = 132.32;
			 data [2][0] = 185.25;
			 data [3][0] = 223.67;
			 data [4][0] = 345.21;
			 
			 return data;
	  }
	  
		/**
		 * this method executes after each test method to close the driver
		 */
		@AfterMethod
		public void tearDown() {
			
			driver.quit();
		}
		

}
