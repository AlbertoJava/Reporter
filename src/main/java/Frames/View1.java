package Frames;

import Utilz.BaseConstants;
import Utilz.SqlProperties;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.core.ZipFile;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;


public class View1 {
    private static HashMap<SqlProperties,Boolean> statusmap = new HashMap<>();
    private static MyFrame frame;
    private static int counter;

    public static void main(String[] args){

      JFrame frame0= new StartUpWindow();
      frame0.setVisible(true);

     /* JFrame frame= new JFrame ("FRAME");
      frame.setSize(new Dimension(600,400));
      frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
      frame.setLocationRelativeTo(null);
      frame.setLayout(new GridBagLayout());


        ProccessesPanelTab ppt = new ProccessesPanelTab();

        frame.add(ppt,new GridBagConstraints(0,0,1,1,1,1,
                GridBagConstraints.NORTH,GridBagConstraints.BOTH,
                new Insets(1,1,1,1),0,0));
        initWorkingPool(false);
      //  ppt.init(statusmap);
       frame.setVisible(true);
       frame.pack();*/


    }
private void test1 (){
    try {
        readZipFile();
    } catch (ZipException e) {
        e.printStackTrace();
    }
    printMap();
    initWorkingPool(false);
    System.out.println("--------------------");
    printMap();
    SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
            frame = new MyFrame("Hello world of SWING!",statusmap);
            frame.setSize(1500,500);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        }
    });

    /*ожидание генерации формы*/
    try {
        Thread.sleep (1000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    //frame.getProccessesPanel().updateProccessList();
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
                    statusmap.put(prop,false);
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
                statusmap.put(prop,false);
            }
        }
    }
    private static void printMap(){
        for (HashMap.Entry<SqlProperties,Boolean> pair:
                statusmap.entrySet()) {
            System.out.println( "-------------------->>>>");
            System.out.println( pair.getKey().getProperty("description"));
            System.out.println( pair.getKey().getProperty("sql"));
        }
    }
}
