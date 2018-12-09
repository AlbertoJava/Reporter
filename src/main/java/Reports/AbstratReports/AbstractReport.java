package Reports.AbstratReports;

import Utilz.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class AbstractReport implements Report,  Runnable {
    private SqlProperties props;
    private Thread thread;
    private SqlExecutor sqlExecutor;
    private final static int crashWaitingTime=1;/*minutes*/

    public AbstractReport(SqlProperties props, SqlExecutor sqlExecutor)  {
        this.sqlExecutor=sqlExecutor;
        this.props = props;
        thread = new Thread (this, getProperty("description"));
        thread.start();
    }


    public Connection getConnection()  {
        return ConnectorToOracle.getInstance().getConnection(props.getProperty("server"));
    }

    public void closeConnection(ResultSet result){
            try {
                Statement stm = result.getStatement();
                result.close();
                stm.close();
                ConnectorToOracle.getInstance().closeConnection(getProperty("server"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    protected ResultSet executeSqlClause (String sqlClause){
        ResultSet resultSet=null;
        try {
            Connection conn = getConnection();
 /*           if (conn ==null){
                try {
                    System.out.println("Due to connection error thread "+ props.getProperty("description") + "  asleep for " +crashWaitingTime+" min!");
                    thread.sleep(crashWaitingTime*60*1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                return null;
            }*/
            Statement stm = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stm.execute(sqlClause);
            resultSet = stm.getResultSet();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
        finally {
            if (resultSet==null ){
                System.out.println("Due to connection error thread "+ props.getProperty("description") + "  asleep for " +crashWaitingTime+" min!");
                try {
                    thread.sleep(crashWaitingTime*60*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
               }
            }
        }
        return resultSet;
    }

    private boolean prepareCreation(){
        if (createReport()) {
         return   getProps().updatePeriodinFile();
        }
        return false;
    }


    @Override
    public void run() {
        while (!prepareCreation()){

        }
        sqlExecutor.addQueue(this);
    }

    public String getProperty(String propsName) {
        return props.getProperty(propsName);
    }

    @Override
    public Long getSleepingTime() {
        return getProps().calcSleepingTime();
    }

    @Override
    public void addProperty(String key, String value) {
       /*if (props.getProperty(key) == null)*/ props.addProperty(key,value);
    }

    public SqlProperties getProps() {
        return props;
    }
}
