package Frames;

import Utilz.BaseConstants;
import Utilz.Desipher;
import Utilz.SqlProperties;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.core.ZipFile;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.swing.*;
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

    public static void main(String[] args){
      //JFrame frame= new LogginWindow();
       /*encoding  to hex*/
        StringBuilder data= new StringBuilder("01.01.2019");
        String encode =  Desipher.encodeData(data.toString());
        System.out.println(encode);

        System.out.println(Desipher.decodeData(encode));

     /*  Calendar c= new GregorianCalendar();
       c.set(
               Integer.valueOf(data.substring(6,10)),
               Integer.valueOf(data.substring(3,5)),
               Integer.valueOf(data.substring(0,2))
       );
       int year = c.get(Calendar.YEAR);
       int month = c.get (Calendar.MONTH);
       int day = c.get (Calendar.DAY_OF_MONTH);
        System.out.println("day " + day);
        System.out.println("month " + month);
        System.out.println("year " + year);

        String offsetedData =  Integer.toString(year,16) +'x' +Integer.toString(month,16) +'x' + Integer.toString(day,16);
        System.out.println(offsetedData);
        System.out.println(Integer.toString(year,16));
        System.out.println(Integer.toString(month,16));
        System.out.println(Integer.toString(day,16));
        offsetedData = offsetedData.substring(1,offsetedData.length()) + offsetedData.substring(0,1);
        System.out.println(offsetedData);

        *//*decoding from hex*//*
        String decodedData = offsetedData.substring(offsetedData.length()-1,offsetedData.length()) +
                                offsetedData.substring(0,offsetedData.length()-1);
        System.out.println(decodedData);
        String [] dataParts =decodedData.split("x");
        int year_1=  Integer.parseInt(dataParts[0],16);
        int month_1=  Integer.parseInt(dataParts[1],16);
        int day_1=  Integer.parseInt(dataParts[2],16);

        System.out.println(year_1);
        System.out.println(month_1);
        System.out.println(day_1);

        c=new GregorianCalendar(year_1,month_1-1,day_1);
        System.out.println(c.getTime().toString());
        SimpleDateFormat formatter=new SimpleDateFormat("dd.MM.yyyy");
        String currentDate=formatter.format(c.getTime());
        System.out.println(currentDate);







        byte [] b=data.toString().getBytes();
       byte [] b_shifted = new byte[b.length];
       b_shifted[0]=b[b.length-1];
       for (int i=0;i<b.length-1;i++){
           b_shifted[i+1]=b[i];
        }

       char[] hexS=Hex.encodeHex(b_shifted);
        System.out.println(hexS);
        try {
            System.out.println(new String (Hex.decodeHex(hexS)));
        } catch (DecoderException e) {
            e.printStackTrace();
        }
        System.out.println(Hex.encodeHex("16".getBytes()));*/

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
