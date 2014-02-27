package pivotal.au.gemfirexd.performance.test;


import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class StartTest
{
    private String testType;

    public StartTest() throws IOException
    {
        Properties props = new Properties();
        URL propertiesUrl = ClassLoader.getSystemResource("gemfirexdtest.properties");
        props.load(propertiesUrl.openStream());

        testType = (String) props.getProperty("testType");
        System.out.printf("Running test as " + testType + " ... \n");
    }

    public String getTestType()
    {
        return testType;
    }

    public static void main(String[] args) throws Exception
    {
        StartTest test = new StartTest();

        if (test.getTestType().equalsIgnoreCase("insert"))
        {
            MultiThreadInsert insertTest = new MultiThreadInsert();
            insertTest.start();
        }
        else
        {
            MultiThreadQuery queryTest = new MultiThreadQuery();
            queryTest.start();
        }

    }
}
