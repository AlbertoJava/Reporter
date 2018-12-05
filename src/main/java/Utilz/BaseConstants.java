package Utilz;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
    private String zipPsw = "123";
    private static Map<String,DBConnection> dbase = new HashMap<String, DBConnection>();
    private static BaseConstants baseConstants = null;
    private static boolean isZip =false;

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

        dbase.put("SZATP",new DBConnection("post190","post190", "jdbc:oracle:thin:@10.37.0.7:1521:SZATP"));
        dbase.put("CAT",new DBConnection("okts","okts", "jdbc:oracle:thin:@10.37.12.20:1521/WH01"));
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

    class DBConnection {
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
