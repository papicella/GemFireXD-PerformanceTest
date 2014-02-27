package pivotal.au.gemfirexd.performance;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ApplicationContextHolder
{
    private static ClassPathXmlApplicationContext applicationContext;

    public static ClassPathXmlApplicationContext getInstance()
    {
        if(applicationContext == null)
        {
            applicationContext = new ClassPathXmlApplicationContext("classpath:application-context.xml");
        }
        return applicationContext;
    }

    public static ApplicationContext getInstance(String contextLocation)
    {
        if(applicationContext == null)
        {
            applicationContext = new ClassPathXmlApplicationContext(contextLocation);
        }
        return applicationContext;
    }
}
