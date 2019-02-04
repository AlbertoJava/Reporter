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
                Printer.printLog(e);
            } catch (IllegalAccessException e) {
                Printer.printLog(e);
            } catch (ClassNotFoundException e) {
                Printer.printLog("Class not found!!! (report factory)");
                Printer.printLog(e);
            } catch (NoSuchMethodException e) {
                Printer.printLog(e);
            } catch (InvocationTargetException e) {
                Printer.printLog(e);
            }

        }
        else {
            report = new RegularMonitor(prop, sqlExecutor);
            report.addProperty("path", BaseConstants.getPath());
        }
        return report;
    }
}
