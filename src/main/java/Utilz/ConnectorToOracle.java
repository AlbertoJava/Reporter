package Utilz;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

import static java.lang.Thread.sleep;

public class ConnectorToOracle {
    HashMap<String, Connection> conections = new HashMap<>();
    HashMap<String, Integer> counterConections = new HashMap<>();
    private static ConnectorToOracle instance;

    private ConnectorToOracle() {
    }

    /*
    * Singletone
    * */
    public static ConnectorToOracle getInstance() {
        ConnectorToOracle localInstance;
        synchronized (ConnectorToOracle.class) {
            localInstance = instance;
            if (localInstance == null) {
                instance = localInstance = new ConnectorToOracle();
            }

        return localInstance;
    }
    }

    public Connection getConnection (String server) throws SQLException {
        synchronized (ConnectorToOracle.class){
        Integer num = counterConections.get(server);


        if (num != null && num != 0 && !conections.get(server).isClosed()) {
            num++;
            counterConections.put(server, num);
            System.out.println("Count of connections for " + server + " is: " +counterConections.get(server));
            return conections.get(server);
        }
        num=0;
        counterConections.put(server, num);
        DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
        Connection con = DriverManager.getConnection(
                        BaseConstants.getInstance().getConnectionString(server),
                        BaseConstants.getInstance().getLog(server),
                        BaseConstants.getInstance().getPsw(server));
        Printer.printRowToMonitor("Connected Successfully to Oracle instance " + server);
        conections.put(server, con);
        num++;
        counterConections.put(server, num);
        System.out.println("Count of connections for " + server + " is first: " +counterConections.get(server));
        return conections.get(server);
        }
    }

    public boolean closeConnection (String server){
        synchronized (ConnectorToOracle.class){
        Integer num = counterConections.get(server);
        num--;
        counterConections.put(server,num);
        if (counterConections.get(server)>0) return true;
        try {
            conections.get(server).close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    }

}
