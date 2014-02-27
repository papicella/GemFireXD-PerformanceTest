package pivotal.au.gemfirexd.performance.test;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.ApplicationContext;
import pivotal.au.gemfirexd.performance.ApplicationContextHolder;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class MultiThreadInsert
{
    private int RECORDS;
    private int COMMIT_POINT;
    private int nThreads;
    private ApplicationContext context;
    private HikariDataSource ds;

    public MultiThreadInsert() throws IOException, SQLException
    {
        loadProperties();
        initalizePool();
    }

    public void loadProperties() throws IOException
    {
        Properties props = new Properties();
        URL propertiesUrl = ClassLoader.getSystemResource("gemfirexdtest.properties");
        props.load(propertiesUrl.openStream());

        RECORDS = Integer.parseInt((String) props.getProperty("records"));
        COMMIT_POINT = Integer.parseInt((String) props.getProperty("commit_point"));
        nThreads = Integer.parseInt((String) props.getProperty("nThreads"));

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

    @SuppressWarnings("unchecked")
    public void start() throws InterruptedException, SQLException
    {
        System.out.printf("Starting %d threads for %d insert records \n", nThreads, RECORDS);

        final ExecutorService executorService = Executors.newFixedThreadPool(nThreads);

        ArrayList list = new ArrayList();
        for (int i = 0; i < nThreads; i++) {
            list.add(new RunData(i+1));
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
        int counter = 0;
        int increment;
        Connection conn;

        private RunData(int increment)
        {
            this.increment = increment;
        }

        private Connection getConnection() throws SQLException
        {
            return ds.getConnection();
        }

        public void run()
        {
            PreparedStatement stmt = null;
            String sql = "insert into person values (?, ?)";
            int counter = 0, size = 0;
            long startTime, endTime;

            int dataSize = RECORDS / nThreads;
            try
            {
                conn = getConnection();
            }
            catch (Exception e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            System.out.printf("Start: %d  End: %d \n",(dataSize * (increment - 1)), (dataSize * increment));
            try
            {
                stmt = conn.prepareStatement(sql);

                startTime = System.currentTimeMillis();
                for (int i = (dataSize * (increment - 1)); i < (dataSize * increment); i++)
                {
                    counter = counter + 1;
                    size = size + 1;
                    stmt.setInt(1, i);
                    stmt.setString(2, "Person" + i);
                    stmt.addBatch();

                    if (counter % COMMIT_POINT == 0)
                    {
                        stmt.executeBatch();
                        endTime = System.currentTimeMillis();
                        System.out.printf("Insert batch of %d records took | %d | milliseconds\n", size, (endTime - startTime));
                        startTime = System.currentTimeMillis();
                        size = 0;
                    }
                }

    			/* there might be more records so call stmt.executeBatch() prior to commit here */
                stmt.executeBatch();

                //System.out.printf("Number of records submitted %d.\n", counter);


            }
            catch (SQLException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            finally
            {
                if (stmt != null)
                {
                    try
                    {
                        stmt.close();
                    }
                    catch (SQLException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

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
            return counter;
        }

    }

    /**
     * @param args
     * @throws InterruptedException
     * @throws SQLException
     * @throws IOException
     */
    public static void main(String[] args) throws InterruptedException, SQLException, IOException
    {
        // TODO Auto-generated method stub
        MultiThreadInsert test = new MultiThreadInsert();
        test.start();
    }
}

