package Controller;

import Frames.LogginWindow;
import Frames.MyFrame;
import Utilz.*;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

import static java.lang.Thread.sleep;
import static javax.swing.JOptionPane.showMessageDialog;

public class Controller {


    private static PriorityBlockingQueue<SqlProperties> tasksQueue = new PriorityBlockingQueue<>();
    private static SqlExecutor sqlExecutor;
    private static MyFrame frame;


    static public void main(String[] args) {
        try {
            if (!checkLisence(true)) {
                new LogginWindow();
            } else {
                init();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showMessageDialog(null, "Error occured:  " + e.getMessage(), "Error message", JOptionPane.OK_OPTION);
        }
    }

    public static void init() {
        try {
            initWorkingPool(BaseConstants.getInstance().isIsZip());
        } catch (IOException e) {
            e.printStackTrace();
            showMessageDialog(null, "Error occured:  " + e.getMessage(), "Error message", JOptionPane.OK_OPTION);
        }
        new Thread(() -> createThreads()).start();

        frame = new MyFrame("Hello world of SWING!", null);
        frame.setPreferredSize(new Dimension(1500, 500));
        while (sqlExecutor == null) {
            System.out.println("SQLExecuter is null...");
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        frame.getProccessesPanel().init(sqlExecutor.getWorkingPool());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

    private static boolean checkLisence(boolean b) throws IOException {
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
                e.printStackTrace();
                showMessageDialog(null, "Error occured:  " + e.getMessage(), "Error message", JOptionPane.OK_OPTION);
            }
            for (FileHeader fh : headers) {
                if (!fh.isDirectory() && fh.getFileName().equals("liesence.txt")) {
                    try (BufferedReader bf = new BufferedReader(new InputStreamReader(zipFile.getInputStream(fh)))) {
                        return checkKey(bf.readLine());
                    } catch (IOException e) {
                        e.printStackTrace();
                        showMessageDialog(null, "Error occured:  " + e.getMessage(), "Error message", JOptionPane.OK_OPTION);
                    } catch (ZipException e) {
                        e.printStackTrace();
                        showMessageDialog(null, "Error occured:  " + e.getMessage(), "Error message", JOptionPane.OK_OPTION);
                    }

                }
            }
            return false;
        } else {
            return true;
        }
    }

    private static Calendar stringToCalendar(String s) {
        if (s.length() != 8 && s.length() != 10) return null;
        int dd = Integer.parseInt(s.substring(0, 2));
        int mm = Integer.parseInt(s.substring(3, 5));

        int yyyy = s.length() == 8 ? Integer.parseInt("20" + s.substring(6, 8)) : Integer.parseInt(s.substring(6, 10));
        return new GregorianCalendar(yyyy, mm, dd);
    }

    private static void initWorkingPool(boolean isEncrypted) throws IOException {
        try {
            if (isEncrypted) {
                readZipFile();
            } else {
                readFolderSql();
            }
        } catch (ZipException e) {
            e.printStackTrace();
            System.out.println("File " + BaseConstants.getInstance().getZipFileSQL() + " generated I/O exception!");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Folder " + BaseConstants.getInstance().getPathSQL() + " generated I/O exception!");
        }
    }

    private static void readFolderSql() throws IOException {
        SimpleFileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
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

    private static void readZipFile() throws ZipException, IOException {
        String zipFilePath = BaseConstants.getInstance().getZipFileSQL();
        ZipFile zipFile = new ZipFile(zipFilePath);
        if (zipFile.isEncrypted()) {
            zipFile.setPassword(BaseConstants.getInstance().getZipPsw());
        }
        List<FileHeader> headers = zipFile.getFileHeaders();
        for (FileHeader fh : headers) {
            System.out.println("Entry: " + fh.getFileName());
            if (!fh.isDirectory() && fh.getFileName().endsWith(".rep")) {
                SqlProperties prop = new SqlProperties(true);
                prop.loadFromFile(new InputStreamReader(zipFile.getInputStream(fh)), fh.getFileName());
                tasksQueue.put(prop);
            }
        }
    }

    public static void createThreads() {
        if (tasksQueue.size() > 0) {
            Printer.printRowToMonitor(String.valueOf(tasksQueue.size()));
            sqlExecutor = new SqlExecutor(tasksQueue);
            sqlExecutor.run();
        }
    }

    public static boolean checkKey(String text) {
        String decodeData = Desipher.decodeData(text);
        if (decodeData == null) return false;
        Calendar c = stringToCalendar(decodeData);
        if (c == null) return false;
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
            Connection conn = ConnectorToOracle.getInstance().getConnection(pair.getKey());
            if (conn == null) continue;
            Statement stm = null;
            try {
                stm = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                stm.execute(sqlClause);
                ResultSet resultSet = stm.getResultSet();
                resultSet.next();
                sDate = resultSet.getString(1);
                resultSet.close();
                stm.close();
                conn.close();
                break;
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        if (sDate != null) {
            Calendar cal = new GregorianCalendar();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            try {
                cal.setTime(sdf.parse(sDate));
                System.out.println("Oracle date " + sDate);
                return cal;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return Calendar.getInstance();

    }
}