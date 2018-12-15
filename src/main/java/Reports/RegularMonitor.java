package Reports;

import Reports.AbstratReports.AbstractReport;
import Utilz.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;


public  class RegularMonitor extends AbstractReport {
    private String description;

    public RegularMonitor(SqlProperties props, SqlExecutor sqlExecutor) {
        super(props, sqlExecutor);
        this.description = props.getProperty("description");
        Printer.printRowToMonitor("Constructor of RegularMonitor: here starting new thread " + description + ", server name: " + getProperty("server"));
    }

    @Override
    public boolean createReport() {
        boolean flag=true;
        Calendar c = new GregorianCalendar();
        String sCurrentDateTime = c.get(Calendar.DATE) + "." + (c.get(Calendar.MONTH) + 1) + "." + c.get(Calendar.YEAR) + " " +
                c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);

        ResultSet result = executeSqlClause(getProperty("sql"));
        if (result == null) {
            Printer.printRowToMonitor(description + " selection returns null " + sCurrentDateTime);
            return false;
        }

        try {
            result.last();
            if (result.getRow() == 0) {
                Printer.saveResult(getProperty("description"),new StringBuilder("empty selection"));
                return true;
            }
        StringBuilder resultSB = new StringBuilder();
        result.beforeFirst();
        while (result.next()) {
             for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
                resultSB.append(result.getString(i) + " : ");
             }
             resultSB.append("\r\n");
        }
        Printer.saveResult(getProperty("description"),resultSB);
        Printer.printLineToMonitor(resultSB.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            flag=false;
        }
        closeConnection(result);
        return flag;
    }

}