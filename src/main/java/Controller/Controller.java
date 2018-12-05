package Controller;

import Utilz.*;
import Frames.*;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

import static java.lang.Thread.sleep;

public class Controller {

    private static List<SqlProperties> reportsList = new ArrayList<>();
    private static PriorityBlockingQueue<SqlProperties> tasksQueue = new PriorityBlockingQueue<>();
    private static SqlExecutor sqlExecutor;
    private static MyFrame frame;


    static public void main (String [] args) {

        if (!checkLisence(true)){
            new LogginWindow();
            }
            else {
                init();
        }

    }
public static void init (){
    initWorkingPool(BaseConstants.getInstance().isIsZip());
    //updateSqlPropertiesFromFile();
    new Thread (() -> createThreads()).start();

    frame = new MyFrame("Hello world of SWING!",null);
    frame.setPreferredSize(new Dimension(100, 500));
    while(sqlExecutor==null){
        System.out.println("SQLExecuter is null...");
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    sqlExecutor.setPanel(frame.getProccessesPanel());
    frame.getProccessesPanel().setStatusMap(sqlExecutor.getWorkingPool());
    //frame = new MyFrame("Hello world of SWING!",null);
    //frame.setSize(500,400);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
    sqlExecutor.setPanel(frame.getProccessesPanel());
    frame.setSize(new Dimension (1300, 500));
}
    private static boolean checkLisence(boolean b)  {
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
            }
            for (FileHeader fh : headers) {
                if (!fh.isDirectory() && fh.getFileName().equals("liesence.txt")) {
                    try (BufferedReader bf = new BufferedReader(new InputStreamReader(zipFile.getInputStream(fh)))) {
                        return checkKey(bf.readLine());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ZipException e) {
                        e.printStackTrace();
                    }
                }
            }
            return false;
        }
        else {
            return true;
        }
    }
    private static Calendar stringToCalendar(String s){
        if (s.length()!=8 && s.length()!=10) return null;
        int dd = Integer.parseInt(s.substring(0,2));
        int mm = Integer.parseInt(s.substring(3,5));

        int yyyy= s.length()==8?Integer.parseInt("20" + s.substring(6,8)) :Integer.parseInt(s.substring(6,10)) ;
        return new GregorianCalendar(yyyy,mm,dd);
    }

    private static void initWorkingPool(boolean isEncrypted){
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
        SimpleFileVisitor<Path> visitor = new SimpleFileVisitor<Path>(){
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
        Files.walkFileTree(Paths.get(BaseConstants.getInstance().getPathSQL()), visitor );
    }

    private  static void readZipFile () throws  ZipException {
        String zipFilePath = BaseConstants.getInstance().getZipFileSQL();
        // zipFilePath="C:\\Java\\18.3.2018.zip";
        ZipFile zipFile = new ZipFile(zipFilePath);
        if (zipFile.isEncrypted()){
            zipFile.setPassword(BaseConstants.getInstance().getZipPsw());
        }
        List<FileHeader> headers =zipFile.getFileHeaders();
        for (FileHeader fh:headers) {
            System.out.println("Entry: " + fh.getFileName());
            if (!fh.isDirectory() && fh.getFileName().endsWith(".rep")) {
                SqlProperties prop = new SqlProperties(true);
                prop.loadFromFile(new InputStreamReader(zipFile.getInputStream(fh)),fh.getFileName());
                tasksQueue.put(prop);
            }
        }
    }
    public static void  createThreads (){
        if (tasksQueue.size()>0) {
            Printer.printRowToMonitor(String.valueOf(tasksQueue.size()));
            sqlExecutor=new SqlExecutor(tasksQueue,null);
            sqlExecutor.run();
        }
    }

    public static  boolean checkKey(String text) {
        String decodeData = Desipher.decodeData(text);
        if (decodeData==null) return false;
        Calendar c = stringToCalendar(decodeData);
        if (c==null) return false;
        if (c.getTimeInMillis()-new GregorianCalendar().getTimeInMillis()>=0){
            return true;
        }
        else{
            return false;
        }
    }

}
