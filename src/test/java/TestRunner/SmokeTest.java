package TestRunner;

import com.base.ConfigReader;
import io.cucumber.core.options.RuntimeOptions;
import io.cucumber.tagexpressions.Expression;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import org.testng.annotations.AfterTest;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@CucumberOptions(
        features = {"src/test/resources/features"},
        glue = {"com.stepdefination"}, tags = "@Datatable",
        plugin = { "pretty", "io.qameta.allure.cucumber6jvm.AllureCucumber6Jvm","json:target/cucumberDefault.json" })
public class SmokeTest extends AbstractTestNGCucumberTests {
    @Override
    @DataProvider()
    public Object[][] scenarios() {
        return super.scenarios();
    }

    private String getprojectName;
    private String getBrowser;
    private String getVersion;
    private String getUser;
    private String getEnvironment;
    private String dateString;


//    @AfterMethod()
//    public void AllureReport()  {
//
//        try {
//
//            Runtime.getRuntime().exec(new String[]{"cmd", "/K", "allure generate allure-results --clean -o allure-report"});
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    @AfterTest()
    public void generateReports(){
        ConfigReader configReader = new ConfigReader();
        Properties prop = configReader.init_prop();
        getprojectName = prop.getProperty("projectName");
        getBrowser= prop.getProperty("browser");
        getVersion= prop.getProperty("version");
        getUser= prop.getProperty("user");
        getEnvironment= prop.getProperty("env");
        GenerateAllRunReports("DefaultRun", "Test1");
    }

    public void GenerateAllRunReports(String runName, String testName) {
        try {
            File[] jsons = null;
            if (testName.equalsIgnoreCase("Test1")) {
                jsons = finder("target/");
            }
            if (runName.equalsIgnoreCase("DefaultRun")) {
                Date date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy_H_m_s_a");
                dateString = simpleDateFormat.format(date);
                List<File> defaultRunJSONs = new ArrayList<>();
                assert jsons != null;
                for (File f : jsons) {
                    if (f.getName().contains("cucumberDefault") && f.getName().endsWith(".json")) {
                        defaultRunJSONs.add(f);
                    }
                }
                if (!defaultRunJSONs.isEmpty()) {
                    generateRunWiseReport(defaultRunJSONs, "Default_Run", testName);
                }
            } else if (runName.equalsIgnoreCase("FirstRun")) {
                List<File> firstRunJSONs = new ArrayList<>();
                assert jsons != null;
                for (File f : jsons) {
                    if (f.getName().contains("cucumber1") && f.getName().endsWith(".json")) {
                        firstRunJSONs.add(f);
                    }
                }
                if (firstRunJSONs.size() != 0) {
                    generateRunWiseReport(firstRunJSONs, "First_Re-Run", testName);
                }
            } else if (runName.equalsIgnoreCase("SecondRun")) {
                List<File> secondRunJSONs = new ArrayList<>();
                assert jsons != null;
                for (File f : jsons) {
                    if (f.getName().contains("cucumber2") && f.getName().endsWith(".json")) {
                        secondRunJSONs.add(f);
                    }
                }
                if (secondRunJSONs.size() != 0) {
                    generateRunWiseReport(secondRunJSONs, "Second_Re-Run", testName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generateRunWiseReport(List<File> jsons, String run, String testName) {
        String projectDir = System.getProperty("user.dir");
        String reportsDir = null;
        if (projectDir != null) {
            String[] ss = projectDir.split("\\\\");
            if (ss.length != 0) {
                String[] stimestamp = ss[ss.length - 1].split("_");
                reportsDir = stimestamp[stimestamp.length - 1];
            }
        }

        try {
            //Adding tag name to the Reports folder name in case there is a single tag.
            RuntimeOptions runtimeOptions = RuntimeOptions.defaultOptions();

            List<Expression> tags = runtimeOptions.getTagExpressions();
            String folderName = "./Reports_Sample/Reports_";
            if (tags.size()== 1) {
                folderName = "./Reports_"+getUser+"/Reports_" + tags.get(0).toString().replace("@", "") + "_";
            }
            File rd = new File(folderName + dateString );
            List<String> jsonReports = new ArrayList<>();
            for (File json : jsons) {
                jsonReports.add(json.getAbsolutePath());
            }
            Configuration configuration = new Configuration(rd, getprojectName);
            configuration.addClassifications("Browser", getBrowser);
            configuration.addClassifications("Version", getVersion);
            configuration.addClassifications("User",getUser );
            configuration.addClassifications("Environment", getEnvironment);
            ReportBuilder reportBuilder = new ReportBuilder(jsonReports, configuration);
            reportBuilder.generateReports();
            System.out.println(run + " consolidated reports are generated under directory " + reportsDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File[] finder(String dirName) {
        File dir = new File(dirName);
        return dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".json");
            }
        });
    }

}
