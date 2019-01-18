package Utilz;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public  class BaseConstants {
    /*Initinalizating parameters*/
    private final static String startFile = "c:\\Java\\monitor.ini";
    private  static String path = "C:\\Java\\";
    private  String pathSQL = "C:\\Java\\SQL\\";
    private  String zipFileSQL = "C:\\Java\\SQL.zip";
    private String localExcelReportPath = "C:\\RegularReports\\";
    private static String liesencePath="C:\\Java\\SQL\\liesence.txt";
    public final static String logFile = "C:\\Java";

    public static String getLiesencePath() {
        return liesencePath;
    }

    public static void setLiesencePath(String liesencePath) {
        BaseConstants.liesencePath = liesencePath;
    }

    private String zipPsw = "123";
    public final static int MAX_COUNT_THREADS=5;

    public static Map<String, DBConnection> getDbase() {
        return dbase;
    }

    private static Map<String,DBConnection> dbase = new HashMap<String, DBConnection>();
    private static BaseConstants baseConstants = null;
    private static boolean isZip =false;

    private BaseConstants() {
        Properties startProps = new Properties();
        try {
            startProps.load(new InputStreamReader(new FileInputStream(new File(startFile)), Charset.forName("UTF-8")));
        } catch (IOException e) {
            e.printStackTrace(); Printer.saveLogFile(e); ;
        }
        // String path, String pathSQL, String cat, String log, String psw
        path = startProps.getProperty("path");
        pathSQL = startProps.getProperty("pathSQL");
        isZip = Boolean.valueOf(startProps.getProperty("isZip").trim());
        zipFileSQL=startProps.getProperty("map")==null?zipFileSQL:startProps.getProperty("map");

        dbase.put("SZATP",new DBConnection("post190","post190", "jdbc:oracle:thin:@10.37.0.7:1521:SZATP"));
        dbase.put("CAT",new DBConnection("okts","okts", "jdbc:oracle:thin:@10.37.12.20:1521/WH01"));
        dbase.put("CAT1",new DBConnection("opsur","opsur", "jdbc:oracle:thin:@10.37.12.20:1521/WH01"));
        dbase.put("HOME",new DBConnection("test","test", "jdbc:oracle:thin:@localhost:1521:orcle"));
        dbase.put("HOME1",new DBConnection("test","test", "jdbc:oracle:thin:@localhost:1521:orcle"));
    }

    public static synchronized BaseConstants getInstance(){
        if (baseConstants ==null) baseConstants =new BaseConstants();
        return baseConstants;
    }

    public String getPsw(String server){
        return dbase.get(server).getPassword();
    }

    public String getLog (String server){
        return dbase.get(server).getLog();
    }

    public String getConnectionString (String server){
        return dbase.get(server).getConnString();
    }

    public static String getPath() {
        return path;
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

    public static boolean isIsZip() {
        return isZip;
    }

    public String getLocalExcelReportPath (){return localExcelReportPath;}

    public class DBConnection {
        private String log;
        private String psw;
        private String connString;

       public String getLog() {
           return log;
       }

       public String getPassword() {
           return psw;
       }

       public String getConnString() {
           return connString;
       }

       public DBConnection(String log, String psw, String connString) {
            this.log = log;
            this.psw = psw;
            this.connString = connString;
        }
    }

}
