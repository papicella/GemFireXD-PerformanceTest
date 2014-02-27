package pivotal.au.gemfirexd.performance.test;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.ApplicationContext;
import pivotal.au.gemfirexd.performance.ApplicationContextHolder;

public class MultiThreadQuery
{

    private int nThreads;
    private int numberOfQueries;
    private int start;
    private int end;

    private ApplicationContext context;
    private HikariDataSource ds;

    public MultiThreadQuery() throws IOException, SQLException
    {
        loadProperties();
        //initialze pool
        initalizePool();
    }

    public void loadProperties() throws IOException
    {
        Properties props = new Properties();
        URL propertiesUrl = ClassLoader.getSystemResource("gemfirexdtest.properties");
        props.load(propertiesUrl.openStream());

        nThreads = Integer.parseInt((String) props.getProperty("nThreads"));
        numberOfQueries = Integer.parseInt((String) props.getProperty("number_of_queries_per_thread"));
        start = Integer.parseInt((String) props.getProperty("start_of_record_range"));
        end = Integer.parseInt((String) props.getProperty("end_of_record_range"));
    }

    public void initalizePool () throws SQLException
    {
        context = getContext();
        ds = (HikariDataSource) context.getBean("dataSource");

        // added just so we create the pool
        Connection conn = ds.getConnection();
        conn.close();

        System.out.printf("Connection Pool created...\n");

    }

    public ApplicationContext getContext()
    {
        return ApplicationContextHolder.getInstance();
    }

    public void start() throws InterruptedException
    {
        System.out.printf("Starting %d threads running %d queries \n", nThreads, numberOfQueries);

        final ExecutorService executorService = Executors.newFixedThreadPool(nThreads);

        ArrayList list = new ArrayList();
        for (int i = 0; i < nThreads; i++)
        {
            list.add(new RunData());
        }
        long start = System.currentTimeMillis();

        List<Future<?>> tasks = executorService.invokeAll(list, 5, TimeUnit.MINUTES);

        for(Future<?> f : tasks){
            try {
                f.get();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        long end = System.currentTimeMillis() - start;

        float elapsedTimeSec = end/1000F;

        System.out.println(String.format("Elapsed time in seconds %f", elapsedTimeSec));

        executorService.shutdown();
        System.exit(0);
    }

    private class RunData implements Callable
    {

        private Random random = null;
        private Connection conn = null;

        private RunData()
        {
            random = new Random();
        }

        private Connection getConnection() throws SQLException
        {
            return ds.getConnection();
        }

        private int getRandomInteger(int aStart, int aEnd, Random aRandom)
        {
            if ( aStart > aEnd )
            {
                throw new IllegalArgumentException("Start cannot exceed End.");
            }

            //get the range, casting to long to avoid overflow problems
            long range = (long)aEnd - (long)aStart + 1;
            // compute a fraction of the range, 0 <= frac < range
            long fraction = (long)(range * aRandom.nextDouble());
            int randomNumber =  (int)(fraction + aStart);

            return randomNumber;
        }

        private void performUpdate ()
        {
            long startTime, endTime;
            String sql = "update person set name = 'updated' where id = ?";
            PreparedStatement pstmt =null;

            int id = getRandomInteger(start, end, random);

            // run query here
            try
            {
                startTime = System.currentTimeMillis();
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
                conn.commit();
                endTime = System.currentTimeMillis();
                System.out.printf("Update with id %d took | %d | milliseconds\n", id, (endTime - startTime));
            }
            catch (SQLException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            finally
            {
                if (pstmt != null)
                {
                    try
                    {
                        pstmt.close();
                    }
                    catch (SQLException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }


        }

        private void performQuery()
        {
            long startTime, endTime;
            String sql = "select id, name from person where id = ?";
            PreparedStatement pstmt =null;
            ResultSet rset = null;

            int id = getRandomInteger(start, end, random);

            // run query here
            try
            {
                startTime = System.currentTimeMillis();
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, id);
                rset = pstmt.executeQuery();
                int numrows = 0;
                while (rset.next())
                {
                    numrows++;
                }

                endTime = System.currentTimeMillis();
                System.out.printf("Query with id %d took | %d | milliseconds\n", id, (endTime - startTime));
            }
            catch (SQLException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            finally
            {
                if (pstmt != null)
                {
                    try
                    {
                        pstmt.close();
                    }
                    catch (SQLException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

        }

        public void run()
        {

            try
            {
                conn = getConnection();
                // run either updfates or query here
                for (int i = 1; i <= numberOfQueries; i++)
                {
                    int id = getRandomInteger(1, 2, random);
                    // if 1 = query, if 2 == update
                    if (id == 1)
                    {
                        // query
                        performQuery();
                    }
                    else
                    {
                        performUpdate();
                    }

                }
            }
            catch (SQLException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            finally
            {
                if (conn != null)
                {
                    try
                    {
                        conn.close();
                    }
                    catch (SQLException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

        }

        public Object call() throws Exception
        {
            run();

            return null;
        }

    }

    /**
     * @param args
     * @throws InterruptedException
     * @throws SQLException
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, SQLException, InterruptedException
    {
        MultiThreadQuery test = new MultiThreadQuery();
        test.start();
    }
}
