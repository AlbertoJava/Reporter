package Reports;

import Reports.AbstratReports.Report;
import Utilz.BaseConstants;
import Utilz.Printer;
import Utilz.SqlExecutor;
import Utilz.SqlProperties;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static Utilz.Printer.printRowToMonitor;

public class ReportFactory {
    public static Report create (SqlProperties prop, SqlExecutor sqlExecutor)  {
        Report report=null;
        if (prop.getProperty("excel")!=null) {
            try {
                Constructor c =Class.forName("Reports." + prop.getProperty("description").trim()).getConstructor(SqlProperties.class, SqlExecutor.class);
                report = (Report)c.newInstance(prop,sqlExecutor);
                report.addProperty("path", prop.getProperty("excel"));
            } catch (InstantiationException e) {
                e.printStackTrace(); Printer.saveLogFile(e);
            } catch (IllegalAccessException e) {
                e.printStackTrace(); Printer.saveLogFile(e);
                ;
            } catch (ClassNotFoundException e) {
                printRowToMonitor("Class not found!!! (report factory)");
                e.printStackTrace(); Printer.saveLogFile(e);
                ;
            } catch (NoSuchMethodException e) {
                e.printStackTrace(); Printer.saveLogFile(e);
                ;
            } catch (InvocationTargetException e) {
                e.printStackTrace(); Printer.saveLogFile(e);
                ;
            }

        }
        else {
            report = new RegularMonitor(prop, sqlExecutor);
            report.addProperty("path", BaseConstants.getPath());
        };
        //report.setIntervalDate(prop.getProperty("date1"), prop.getProperty("date2"));
        return report;
    }
}
