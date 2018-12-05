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
        Printer.printRowToMonitor("Конструктор RegularMonitor: cтартуем поток " + description + ", сервер подключения: " + getProperty("server"));
    }

    @Override
    public boolean createReport() {
        boolean flag=true;
        Calendar c = new GregorianCalendar();
        String sCurrentDateTime = c.get(Calendar.DATE) + "." + (c.get(Calendar.MONTH) + 1) + "." + c.get(Calendar.YEAR) + " " +
                c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);
        Printer.printRowToMonitor(sCurrentDateTime);

        ResultSet result = executeSqlClause(getProperty("sql"));
        if (result == null) {
            Printer.printRowToMonitor(description + " selection returns null " + sCurrentDateTime);
            return false;
        }

        Printer.saveResult(getProperty("description"),null);

        try {
            result.last();
            if (result.getRow() == 0) {
                Printer.saveResult(getProperty("description"),new StringBuilder("empty selection"));
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            flag=false;
        }
        StringBuilder resultSB = new StringBuilder();
        try {
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
        //getProps().updateTimeToStart();
        return flag;
    }












   /* @Override
    public boolean createReport1() {
        boolean flag=true;
        Calendar c = new GregorianCalendar();
        String sCurrentDate = c.get(Calendar.DATE) + "." + (c.get(Calendar.MONTH) + 1) + "." + c.get(Calendar.YEAR);
        String sCurrentDateTime = c.get(Calendar.DATE) + "." + (c.get(Calendar.MONTH) + 1) + "." + c.get(Calendar.YEAR) + " " +
                c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);
        Printer.printRowToMonitor(sCurrentDateTime);
        ResultSet result = executeSqlClause(getProperty("sql"));
        if (result == null) {
            Printer.printRowToMonitor(description + " selection returns null " + sCurrentDateTime);
            return false;
        }
        Path path = Paths.get(BaseConstants.getPath() + "\\" + sCurrentDate + ".txt");
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                e.printStackTrace();
                flag=false;
            }
        }
        try {
            result.last();
            if (result.getRow() == 0) {

                Printer.printRowToMonitor(description + " empty selection " + sCurrentDateTime);
                Files.write(path, (description + " empty selection " + sCurrentDate + "\r\n").getBytes("windows-1251"), StandardOpenOption.APPEND);
                Files.write(path, ("-------------------------------------\r\n").getBytes("windows-1251"), StandardOpenOption.APPEND);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            flag=false;
        } catch (IOException e) {
            e.printStackTrace();
            flag=false;
        }
        StringBuilder resultSB = new StringBuilder();
        try {
            result.beforeFirst();
            Files.write(path, (description + " " + sCurrentDateTime + "\r\n").getBytes("windows-1251"), StandardOpenOption.APPEND);
            resultSB.append("----------" + Thread.currentThread().getName() + " start printing " + sCurrentDateTime + "----------------\r\n");
            while (result.next()) {
                for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
                    resultSB.append(result.getString(i) + " : ");
                }
                resultSB.append("\r\n");
            }
            resultSB.append("----------" + Thread.currentThread().getName() + " finished printing----------------\r\n");

            Files.write(path, resultSB.toString().getBytes(), StandardOpenOption.APPEND);
            Printer.printLineToMonitor(resultSB.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            flag=false;
        } catch (IOException e) {
            e.printStackTrace();
            flag=false;
        }
          closeConnection(result);
        //getProps().updateTimeToStart();
        return flag;
    }*/
   /* public boolean createReport1() {
        boolean flag=true;
        Calendar c = new GregorianCalendar();
        String sCurrentDate = c.get(Calendar.DATE) + "." + (c.get(Calendar.MONTH) + 1) + "." + c.get(Calendar.YEAR);
        String sCurrentDateTime = c.get(Calendar.DATE) + "." + (c.get(Calendar.MONTH) + 1) + "." + c.get(Calendar.YEAR) + " " +
                c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);
        Printer.printRowToMonitor(sCurrentDateTime);
        ResultSet result = executeSqlClause(getProperty("sql"));
        if (result == null) {
            Printer.printRowToMonitor(description + " selection returns null " + sCurrentDateTime);
            return false;
        }
        Path path = Paths.get(BaseConstants.getPath() + "\\" + sCurrentDate + ".txt");
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                e.printStackTrace();
                flag=false;
            }
        }
        try {
            result.last();
            if (result.getRow() == 0) {

                Printer.printRowToMonitor(description + " empty selection " + sCurrentDateTime);
                Files.write(path, (description + " empty selection " + sCurrentDate + "\r\n").getBytes("windows-1251"), StandardOpenOption.APPEND);
                Files.write(path, ("-------------------------------------\r\n").getBytes("windows-1251"), StandardOpenOption.APPEND);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            flag=false;
        } catch (IOException e) {
            e.printStackTrace();
            flag=false;
        }
        StringBuilder resultSB = new StringBuilder();
        try {
            result.beforeFirst();
            Files.write(path, (description + " " + sCurrentDateTime + "\r\n").getBytes("windows-1251"), StandardOpenOption.APPEND);
            resultSB.append("----------" + Thread.currentThread().getName() + " start printing " + sCurrentDateTime + "----------------\r\n");
            while (result.next()) {
                for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
                    resultSB.append(result.getString(i) + " : ");
                }
                resultSB.append("\r\n");
            }
            resultSB.append("----------" + Thread.currentThread().getName() + " finished printing----------------\r\n");

            Files.write(path, resultSB.toString().getBytes(), StandardOpenOption.APPEND);
            Printer.printLineToMonitor(resultSB.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            flag=false;
        } catch (IOException e) {
            e.printStackTrace();
            flag=false;
        }
        closeConnection(result);
        //getProps().updateTimeToStart();
        return flag;
    }*/
}