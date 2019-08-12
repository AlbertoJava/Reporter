package Utilz;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class BaseConstants {
    /*Initinalizating parameters*/
    private final static String startFile = "c:\\Java\\monitor.ini";
    private static String path = "C:\\Java\\";
    private static Map<String, DBConnection> dbase = new HashMap<String, DBConnection>();
    private static BaseConstants baseConstants = null;
    private static boolean isZip = false;
    private String pathSQL = "C:\\Java\\SQL\\";
    private String zipFileSQL = "C:\\Java\\SQL.zip";
    private String localExcelReportPath = "C:\\RegularReports\\";
    private String zipPsw = "123";

    private BaseConstants() {
        Properties startProps = new Properties();
        try {
            startProps.load(new FileInputStream(new File(startFile)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // String path, String pathSQL, String cat, String log, String psw
        path = startProps.getProperty("path");
        pathSQL = startProps.getProperty("pathSQL");
        isZip = Boolean.valueOf(startProps.getProperty("isZip").trim());

        dbase.put("SZATP", new DBConnection("post190", "post190", "jdbc:oracle:thin:@10.37.0.7:1521:SZATP"));
        dbase.put("CAT", new DBConnection("okts", "okts", "jdbc:oracle:thin:@10.37.12.20:1521/WH01"));
        dbase.put("HOME", new DBConnection("test", "test", "jdbc:oracle:thin:@localhost:1521:orcle"));
        dbase.put("HOME1", new DBConnection("test", "test", "jdbc:oracle:thin:@localhost:1521:orcle"));
    }

    public static Map<String, DBConnection> getDbase() {
        return dbase;
    }

    public static synchronized BaseConstants getInstance() {
        if (baseConstants == null) baseConstants = new BaseConstants();
        return baseConstants;
    }

    public static String ms_totime(long ms) {
        Integer[] time = new Integer[4];
        time[3] = (int) ms / 86400000;
        ms = ms % 86400000;
        time[2] = (int) ms / 3600000;
        ms = ms % 3600000;
        time[1] = (int) ms / 60000;
        ms = ms % 60000;
        time[0] = (int) ms / 1000;
        ms = ms % 1000;
        StringBuilder formatted_time = new StringBuilder();
        for (int i = time.length - 1; i >= 0; i--) {
            if (time[i].toString().length() < 2) {
                formatted_time.append('0');
            }
            formatted_time.append(time[i]);
            formatted_time.append(":");
        }
        formatted_time.deleteCharAt(formatted_time.length() - 1);
        //System.out.println(formatted_time);
        return formatted_time.toString();
    }

    public static String getPath() {
        return path;
    }

    public static boolean isIsZip() {
        return isZip;
    }

    public String getPsw(String server) {
        return dbase.get(server).getPassword();
    }

    public String getLog(String server) {
        return dbase.get(server).getLog();
    }

    public String getConnectionString(String server) {
        return dbase.get(server).getConnString();
    }

    public String getPathSQL() {
        return pathSQL;
    }

    public String getZipFileSQL() {
        return zipFileSQL;
    }

    public String getZipPsw() {
        return zipPsw;
    }

    public String getLocalExcelReportPath() {
        return localExcelReportPath;
    }

    public class DBConnection {
        private String log;
        private String psw;
        private String connString;

        public DBConnection(String log, String psw, String connString) {
            this.log = log;
            this.psw = psw;
            this.connString = connString;
        }

        public String getLog() {
            return log;
        }

        public String getPassword() {
            return psw;
        }

        public String getConnString() {
            return connString;
        }
    }

}
