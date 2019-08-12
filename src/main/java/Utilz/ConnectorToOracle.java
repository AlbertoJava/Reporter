package Utilz;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

public class ConnectorToOracle {
    private static ConnectorToOracle instance;
    HashMap<String, Connection> conections = new HashMap<>();
    HashMap<String, Integer> counterConections = new HashMap<>();

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

    public Connection getConnection(String server) {
        synchronized (ConnectorToOracle.class) {
            Integer num = counterConections.get(server);
            Connection con;
            if (num != null && num != 0) {
                num++;
            } else {
                try {
                    DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
                    con = DriverManager.getConnection(
                            BaseConstants.getInstance().getConnectionString(server),
                            BaseConstants.getInstance().getLog(server),
                            BaseConstants.getInstance().getPsw(server));
                    Printer.printRowToMonitor("Connected Successfully to Oracle instance " + server);
                    conections.put(server, con);
                    num = 1;
                } catch (SQLException e) {
                    Printer.printRowToMonitor("Error when connect to Oracle instance " + server);
                    e.printStackTrace();
                } finally {

                }
            }
            counterConections.put(server, num);
            return conections.get(server);
        }
    }

    public boolean closeConnection(String server) {
        synchronized (ConnectorToOracle.class) {
            Integer num = counterConections.get(server);
            num--;
            counterConections.put(server, num);
            if (counterConections.get(server) > 0) return true;
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
