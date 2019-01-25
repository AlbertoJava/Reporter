package Controller;

import Utilz.*;
import Frames.*;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

import static Utilz.Printer.printRowToMonitor;
import static java.lang.Thread.sleep;

public class Controller {


    private static PriorityBlockingQueue<SqlProperties> tasksQueue = new PriorityBlockingQueue<>();
    private static SqlExecutor sqlExecutor;
    private static MyFrame frame;
    private static StartUpWindow startFrame;
    private  static StringCrypter stringCrypter = new StringCrypter();


    static public void main(String[] args) {
        BaseConstants.getInstance();
        startFrame = new StartUpWindow();
        startFrame.setVisible(true);
        if (!checkLisence(BaseConstants.isIsZip())) {
            startFrame.dispose();
            new LicenseWindow();
        } else {
            startFrame.dispose();
           SwingUtilities.invokeLater(new Runnable() {
               @Override
               public void run() {
                   init(BaseConstants.getInstance().isIsZip());
               }
           });

        }
    }

    public static void init(boolean isZip) {
        initWorkingPool(isZip);
        // Here starts sqlExecuter with tasksQueue
        new Thread(() -> createSqlExecuter()).start();
        frame = new MyFrame("Hello world of SWING!", null);
        frame.setPreferredSize(new Dimension(1500, 500));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getProccessesPanel().init(sqlExecutor.getWaitingQueue());
        frame.pack();
        frame.setVisible(true);

    }

    private static boolean checkLisence(boolean b) {
        if (b) {
            String zipFilePath = BaseConstants.getInstance().getZipFileSQL();
            ZipFile zipFile = null;
            List<FileHeader> headers = null;
            try {
                zipFile = new ZipFile(zipFilePath);
                if (zipFile.isEncrypted()) {
                    zipFile.setPassword(BaseConstants.getInstance().getZipPsw());
                    headers = zipFile.getFileHeaders();
                }
            } catch (ZipException e) {
                e.printStackTrace(); Printer.saveLogFile(e); ;
                ;
            }
            for (FileHeader fh : headers) {
                if (!fh.isDirectory() && fh.getFileName().endsWith("liesence.txt")) {
                    BaseConstants.setLiesencePath(fh.getFileName());
                    try (BufferedReader bf = new BufferedReader(new InputStreamReader(zipFile.getInputStream(fh), Charset.forName("UTF-8")))) {
                        return checkKey(bf.readLine());
                    } catch (IOException e) {
                        e.printStackTrace(); Printer.saveLogFile(e); ;
                        ;
                    } catch (ZipException e) {
                        e.printStackTrace(); Printer.saveLogFile(e); ;
                        ;
                    }
                }
            }
            return false;
        } else {
           try (BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(BaseConstants.getLiesencePath()),Charset.forName("UTF-8"))))
           {return checkKey(bf.readLine());
           } catch (FileNotFoundException e) {
               e.printStackTrace(); Printer.saveLogFile(e);
           } catch (IOException e) {
               e.printStackTrace(); Printer.saveLogFile(e);
           }

        }
            return false;
    }

    private static Calendar stringToCalendar(String s) {
        if (s.length() != 8 && s.length() != 10) return null;
        int dd = Integer.parseInt(s.substring(0, 2));
        int mm = Integer.parseInt(s.substring(3, 5));

        int yyyy = s.length() == 8 ? Integer.parseInt("20" + s.substring(6, 8)) : Integer.parseInt(s.substring(6, 10));
        return new GregorianCalendar(yyyy, mm, dd);
    }

    /* Initalizing tasksQueue with objects of SqlProperties. SqlPropeties reads from report files*/
    private static void initWorkingPool(boolean isEncrypted) {
        try {
            if (isEncrypted) {
                readZipFile();
            } else {
                readFolderSql();
            }
        } catch (ZipException e) {
            e.printStackTrace(); Printer.saveLogFile(e); ;
            ;
            printRowToMonitor("File " + BaseConstants.getInstance().getZipFileSQL() + " generated I/O exception!");
        } catch (IOException e) {
            e.printStackTrace(); Printer.saveLogFile(e); ;
            ;
            printRowToMonitor("Folder " + BaseConstants.getInstance().getPathSQL() + " generated I/O exception!");
        }
    }

    private static void readFolderSql() throws IOException {
        SimpleFileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".rep")) {
                    SqlProperties prop = new SqlProperties(false);
                    prop.loadFromFile(file.toFile());
                    tasksQueue.put(prop);
                }
                return FileVisitResult.CONTINUE;
            }
        };
        Files.walkFileTree(Paths.get(BaseConstants.getInstance().getPathSQL()), visitor);
    }

    private static void readZipFile() throws ZipException {
        String zipFilePath = BaseConstants.getInstance().getZipFileSQL();
        // zipFilePath="C:\\Java\\18.3.2018.zip";
        ZipFile zipFile = new ZipFile(zipFilePath);
        if (zipFile.isEncrypted()) {
            zipFile.setPassword(BaseConstants.getInstance().getZipPsw());
        }
        List<FileHeader> headers = zipFile.getFileHeaders();
        for (FileHeader fh : headers) {
            printRowToMonitor("Entry: " + fh.getFileName());
            if (!fh.isDirectory() && fh.getFileName().endsWith(".rep")) {
                SqlProperties prop = new SqlProperties(true);
                prop.loadFromFile(new InputStreamReader(zipFile.getInputStream(fh),Charset.forName("UTF-8")), fh.getFileName());
                tasksQueue.put(prop);
            }
        }
    }

    public static void createSqlExecuter() {
        if (tasksQueue.size() > 0) {
            printRowToMonitor(String.valueOf(tasksQueue.size()));
            sqlExecutor = new SqlExecutor(tasksQueue);
            sqlExecutor.run();
        }
    }

    public static boolean checkKey(String text) {
        String decodeData = stringCrypter.decrypt(text);
        if (decodeData == null) return false;
        Calendar c = stringToCalendar(decodeData);
        if (c == null) return false;
        c.add(Calendar.MONTH,-1);
        if (c.getTimeInMillis() - getCurrentDate().getTimeInMillis() >= 0) {
            return true;
        } else {
            return false;
        }
    }

    private static Calendar getCurrentDate() {
        String sqlClause = "select to_char (current_date, 'dd.MM.yyyy') from dual";
        String sDate = null;
        for (Map.Entry<String, BaseConstants.DBConnection> pair :
                BaseConstants.getDbase().entrySet()) {
            try {
                Connection conn = ConnectorToOracle.getInstance().getConnection(pair.getKey());
                if (conn==null) continue;
                Statement stm = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                stm.execute(sqlClause);
                ResultSet resultSet = stm.getResultSet();
                resultSet.next();
                sDate = resultSet.getString(1);
                resultSet.close();
                stm.close();
                conn.close();
                break;
            } catch (SQLException e) {
                e.printStackTrace(); Printer.saveLogFile(e); ;
                ;
            }

        }

        if (sDate != null) {
            Calendar cal = new GregorianCalendar();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            try {
                cal.setTime(sdf.parse(sDate));
                printRowToMonitor("Oracle date " + sDate);
                return cal;
            } catch (ParseException e) {
                e.printStackTrace(); Printer.saveLogFile(e); ;
                ;
            }
        }
        return Calendar.getInstance();

    }
}