
import com.cucumber.listener.Reporter;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.AfterClass;
import org.junit.runner.RunWith;


@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/java",glue="CucumberTest",
        plugin = { "com.cucumber.listener.ExtentCucumberFormatter:target/cucumber-reports/report.html" })
public class TestRunner {

    @AfterClass
    public static void writeExtentReport() {
        Reporter.loadXMLConfig("src/extent-config.xml");
    }
}
