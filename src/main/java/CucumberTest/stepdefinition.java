package CucumberTest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import cucumber.api.DataTable;
import cucumber.api.Scenario;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.Select;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class stepdefinition {
    WebDriver driver;
    ExtentTest test;
    ExtentReports report;
    Scenario scenario;

    public stepdefinition(){

        report = new ExtentReports(System.getProperty("user.dir")+"\\ExtentReportResults.html");
        test = report.startTest("test");

    }




    public JSONObject readYAML() throws IOException, ParseException{
        String path = System.getProperty("user.dir");
        String yaml = new String(Files.readAllBytes(Paths.get( path + "\\src\\main\\resources\\Objects.yaml")));
        ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
        Object obj = yamlReader.readValue(yaml, Object.class);
        ObjectMapper jsonWriter = new ObjectMapper();
        JSONParser j = new JSONParser();
        JSONObject jsonobject = (JSONObject)j.parse(jsonWriter.writerWithDefaultPrettyPrinter().writeValueAsString(obj));
        JSONObject ObjectRepo = (JSONObject)jsonobject.get("ObjectRepository");
        return ObjectRepo;
    }
    public List<String> ReadingTypeAndValue(String Pagename, String Elementname) throws IOException, ParseException {
        List<String> TypeAndValue = new ArrayList<>();
        JSONObject repository = readYAML();
        JSONObject ele = (JSONObject)repository.get(Pagename);
        JSONObject typeAndValue = (JSONObject)ele.get(Elementname);
        TypeAndValue.add(typeAndValue.get("type").toString());
        TypeAndValue.add(typeAndValue.get("value").toString());
        return TypeAndValue;

    }
    public String RetrievefieldValue(String fieldName) throws IOException, ParseException {
        JSONObject repository = readYAML();
        String fieldValue = repository.get(fieldName).toString();
        return fieldValue;

    }
    public void driverInitialisation() throws IOException, ParseException {
        String browser = RetrievefieldValue("Browser");
        String headless = RetrievefieldValue("Headless");
        if(headless.equalsIgnoreCase("True")){
            String path = System.getProperty("user.dir");
            System.setProperty("webdriver.chrome.driver", path + "\\Drivers\\chromedriver.exe");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("headless");
            driver = new ChromeDriver(options);

        }else{
            if(browser.equalsIgnoreCase("chrome")){
                System.out.println("Executing Before");
                String path = System.getProperty("user.dir");
                System.setProperty("webdriver.chrome.driver", path + "\\Drivers\\chromedriver.exe");
                driver = new ChromeDriver();
                driver.manage().window().maximize();
                driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

            }else if(browser.equalsIgnoreCase("ie")){
                System.out.println("Executing Before");
                String path = System.getProperty("user.dir");
                System.setProperty("webdriver.ie.driver", path + "\\Drivers\\IEDriverServer.exe");
                driver = new InternetExplorerDriver();
                driver.manage().window().maximize();
                driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

            }
        }


    }
    public String captureScreen() throws IOException {
        TakesScreenshot screen = (TakesScreenshot) driver;
        File src = screen.getScreenshotAs(OutputType.FILE);
        String path = System.getProperty("user.dir");
        String dest = path+"\\screenshots\\" + LocalDate.now().toString() + LocalTime.now().getHour()+ LocalTime.now().getMinute()+ LocalTime.now().getSecond()+ ".png";
        File target = new File(dest);
        FileUtils.copyFile(src,target);
        return dest;
    }
    public WebElement findElement(String Pagename, String ElementName) throws IOException {
        WebElement we= null;
        try{

            List <String> inputs = ReadingTypeAndValue(Pagename,ElementName);
            if(inputs.get(0).equalsIgnoreCase("xpath")){
                we = driver.findElement(By.xpath(inputs.get(1)));
                test.log(LogStatus.PASS, "Identified Webelement successfully with xpath"+test.addScreenCapture(captureScreen()));

            }else if(inputs.get(0).equalsIgnoreCase("id")){
                we =driver.findElement(By.id(inputs.get(1)));
                test.log(LogStatus.PASS, "Identified Webelement successfully with id"+test.addScreenCapture(captureScreen()));
            }
            else if(inputs.get(0).equalsIgnoreCase("name")){
                we = driver.findElement(By.name(inputs.get(1)));
                test.log(LogStatus.PASS, "Identified Webelement successfully with name"+test.addScreenCapture(captureScreen()));
            }


        }catch (Exception e){
            test.log(LogStatus.FAIL, "Unable to locate Webelement"+e.getMessage()+test.addScreenCapture(captureScreen()));

        }
        return we;

    }
    public void clickObject(String Pagename, String ElementName) throws IOException {
        try{
            WebElement element = this.findElement(Pagename,ElementName);
            element.click();
            test.log(LogStatus.PASS, "WebElement clicked successfully"+test.addScreenCapture(captureScreen()));

        }catch (Exception e){
            test.log(LogStatus.FAIL, "Error occured in clicking WebElement"+e.getMessage()+test.addScreenCapture(captureScreen()));

        }


    }
    public void SendKeys(String Pagename, String ElementName,String TexttoSet) throws IOException {
        try{
            WebElement element = this.findElement(Pagename,ElementName);
            element.sendKeys(TexttoSet);
            test.log(LogStatus.PASS, "Text Entered successfully"+test.addScreenCapture(captureScreen()));

        }catch (Exception e){
            test.log(LogStatus.FAIL, "Error occured in entering value to WebElement"+e.getMessage()+test.addScreenCapture(captureScreen()));

        }


    }
    public void navigateToURL(String AppURL) throws IOException {
        try{
            driver.get(this.RetrievefieldValue(AppURL));
            test.log(LogStatus.PASS, "Navigated to the specified URL"+test.addScreenCapture(captureScreen()));

        }catch (Exception e){
            test.log(LogStatus.FAIL, "Error occured in navigate the specified URL"+e.getMessage()+test.addScreenCapture(captureScreen()));

        }


    }
    public void selectValuebyVisibleText(String PageName,String ElementName,String Texttobeselected) throws IOException {
        try{
            Select dropdown = new Select(findElement(PageName,ElementName));
            dropdown.selectByVisibleText(Texttobeselected);
            test.log(LogStatus.PASS, "Selected the value from dropdown using visibleText"+test.addScreenCapture(captureScreen()));

        }catch (Exception e){
            test.log(LogStatus.FAIL, "Error occured in selecting the specified Text from dropdown"+e.getMessage()+test.addScreenCapture(captureScreen()));

        }


    }
    public String getText(String PageName,String ElementName) throws IOException {
        String actualText="";
        try{
            WebElement element = findElement(PageName,ElementName);
            actualText = element.getText();
            test.log(LogStatus.PASS, "Able to get Text from element"+test.addScreenCapture(captureScreen()));

        }catch (Exception e){
            test.log(LogStatus.FAIL, "Error occured in retrieving Text from specified element"+e.getMessage()+test.addScreenCapture(captureScreen()));

        }
        return actualText;

    }


    @Given("^User is in Demo application and able to access Insurance project$")
    public void applicationNavigation() throws IOException, ParseException {

        driverInitialisation();
        navigateToURL("DemoApplicationURL");
        clickObject("MainPage","InsuranceProject");

    }

    @Then("^User should register by providing all the necessary details and Verify it$")
    public void user_should_register_by_providing_all_the_necessary_details(DataTable input) throws IOException, ParseException {
        List<List<String>> data = input.raw();
        Integer random = (int)Math.random();
        clickObject("RegisterPage","Register");
        selectValuebyVisibleText("RegisterPage","Title",data.get(0).get(0));
        SendKeys("RegisterPage","FirstName",data.get(0).get(1));
        SendKeys("RegisterPage","SurName",data.get(0).get(2));
        SendKeys("RegisterPage","Phone",data.get(0).get(3));
        selectValuebyVisibleText("RegisterPage","Year",data.get(0).get(4));
        selectValuebyVisibleText("RegisterPage","Month",data.get(0).get(5));
        selectValuebyVisibleText("RegisterPage","Date",data.get(0).get(6));
        clickObject("RegisterPage","licenceType");
        selectValuebyVisibleText("RegisterPage","licencePeriod",data.get(0).get(7));
        selectValuebyVisibleText("RegisterPage","occupation",data.get(0).get(8));
        SendKeys("RegisterPage","Street",data.get(0).get(9));
        SendKeys("RegisterPage","city",data.get(0).get(10));
        SendKeys("RegisterPage","country",data.get(0).get(11));
        SendKeys("RegisterPage","postCode",data.get(0).get(12));
        String email= data.get(0).get(13)+((random)* (1000-10+1)+10)+"@gmail.com";
        SendKeys("RegisterPage","email",email);
        SendKeys("RegisterPage","password",data.get(0).get(14));
        SendKeys("RegisterPage","ConfirmationPassword",data.get(0).get(14));
        clickObject("RegisterPage","Create");
        SendKeys("RegisterPage","emailsignIn",email);
        SendKeys("RegisterPage","passwordsignIn",data.get(0).get(14));
        clickObject("RegisterPage","Submit");
        String actualEmailId = getText("RegisterPage","EmailStaticText");
        Assert.assertEquals(email,actualEmailId);
        closingbrowser();

    }
    @Given("^User is in Demo application and able to access NewTours$")
    public void Navigation_NewsTours() throws IOException, ParseException {

        driverInitialisation();
        navigateToURL("DemoApplicationURL");
        clickObject("MainPage","NewTours");

    }
    @Then("^User should verify the webtable datas$")
    public void webtablehandling() throws IOException, ParseException {
        List<String> ExpectedValues = new ArrayList<>();
        ExpectedValues.add("Home");ExpectedValues.add("Flights");ExpectedValues.add("Hotels");ExpectedValues.add("Car Rentals");ExpectedValues.add("Cruises");ExpectedValues.add("Destinations");ExpectedValues.add("Vacations");
        WebElement table = findElement("ToursPage","MainMenuTable");
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        WebElement column = rows.get(0).findElements(By.tagName("td")).get(1);
        String actual  = column.findElement(By.tagName("font")).findElement(By.tagName("a")).getText();
        Assert.assertTrue(ExpectedValues.contains(actual));
        closingbrowser();

    }
    
    public void closingbrowser() throws IOException, ParseException {
        if(RetrievefieldValue("Headless").equalsIgnoreCase("False")){
            driver.quit();

        }
        report.endTest(test);
        report.flush();

    }

}
