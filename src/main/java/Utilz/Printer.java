package Utilz;


import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public  class Printer {
    private static ConcurrentMap<String, ResultTable> results=new ConcurrentHashMap();
    private final static Object lock = new Object();

    public static void saveLogFile(Exception e){
        synchronized (lock){
            Path path = Paths.get (getLogFile());
            if (!Files.exists(path)&!Files.isDirectory(path)) {
                try {
                    Files.createFile(path);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            File file = new File (getLogFile());
            try (PrintWriter pw = new PrintWriter(new FileWriter(file,true), true);) {
                e.printStackTrace(pw);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            lock.notifyAll();
        }

    }

    private static String getLogFile() {
        return BaseConstants.logFile + "\\"+StringUtilz.toString(Calendar.getInstance()).replace("/","_") + "_logReporer.txt";
    }

    private static void saveLogFile(String text){
        synchronized (lock) {
            System.out.println(getLogFile());
            Path path = Paths.get(getLogFile());
            if (!Files.exists(path)&!Files.isDirectory(path)) {
                try {
                    Files.createFile(path);
                } catch (IOException e) {
                    e.printStackTrace(); Printer.saveLogFile(e);
                }
            }

            try (FileWriter fw = new FileWriter(path.toString(), true)) {

                // fw.write("--------------- " + StringUtilz.toString(Calendar.getInstance()) + "\n\r--------------- ");
                fw.write(text + "\r\n");
            } catch (IOException e) {
                e.printStackTrace(); Printer.saveLogFile(e);

            }
            lock.notifyAll();
        }
    }

    public  synchronized static void printRowToMonitor(String text){
        System.out.println(text);
        saveLogFile(text);
    }
    public synchronized static  void printLineToMonitor(String text){
        System.out.println(text);
        saveLogFile(text);
    }

    public synchronized static void saveResult(String description, StringBuilder result){
        if (result==null || description==null) return;
        results.put(description,new ResultTable(result, new GregorianCalendar()));
        saveMaptoFile();
    }

    private static boolean saveMaptoFile(){
        Calendar c = new GregorianCalendar();
        String sCurrentDate = c.get(Calendar.DATE) + "." + (c.get(Calendar.MONTH) + 1) + "." + c.get(Calendar.YEAR);

        Path path = Paths.get(BaseConstants.getPath() + "\\" + sCurrentDate + ".txt");
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                e.printStackTrace(); Printer.saveLogFile(e); ;
                return false;
            }
        }
        StringBuilder sb = new StringBuilder();

        for (ConcurrentMap.Entry<String, ResultTable> pair:
             results.entrySet()) {
            sb.append("\r\n");
            StringBuilder header = new StringBuilder();
            header.append("--------------------------------");
            header.append(pair.getKey());
            header.append(' ');
            header.append(pair.getValue().getDateTime());
            header.append("-----------------------------------\r\n");

            sb.append(header);
            sb.append(pair.getValue().getResult());
            String closingHeader = StringUtilz.alignStringAfter("--------------------------------" +pair.getKey(),
                                                           header.length()-2,"-");
            sb.append(closingHeader);
            sb.append("\r\n\r\n\r\n");
        }
/*
        try(FileOutputStream fos = new FileOutputStream(BaseConstants.getPath() + "\\" + sCurrentDate + ".txt","UTF-8"){
            //fout.write("");
            String secondString = new String(sb.toString().getBytes("windows-1251"),"UTF-8");
            while (fos.)

            fout.write (secondString);
        }
         catch (IOException e) {
            e.printStackTrace(); Printer.saveLogFile(e); ;
        }*/

        try {
            Files.write(path,
                        (sb.toString()).getBytes("windows-1251"),
                         StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace(); Printer.saveLogFile(e); ;
            return false;
        }
        return true;
    }

    public static List<StringBuilder> getResults(String description) {
        List<StringBuilder> temp = new ArrayList<StringBuilder>();
        if (results==null||results.get(description)==null) {
            temp.add(new StringBuilder());
            temp.add(new StringBuilder());
        }
        else {
            temp.add(results.get(description).getResult());
            temp.add(results.get(description).getDateTime());
        }
        return temp;
    }

    private static class ResultTable {
        private StringBuilder result;
        private StringBuilder dateTime;

        public ResultTable(StringBuilder result, Calendar c) {
            this.result=result;
            dateTime=toStringBulder(c);
        }

        private StringBuilder toStringBulder(Calendar c) {

            StringBuilder result= new StringBuilder();
            result.append(align(c.get(Calendar.HOUR_OF_DAY)));
            result.append(':');
            result.append(align(c.get(Calendar.MINUTE)));
            result.append(':');
            result.append(align(c.get(Calendar.SECOND)));
            result.append(' ');
            result.append(align(c.get(Calendar.DAY_OF_MONTH)));
            result.append('.');
            result.append(align(c.get(Calendar.MONTH)));
            result.append('.');
            result.append(align(c.get(Calendar.YEAR)));
            return result;
        }

        private StringBuilder align(int i) {
            StringBuilder sb= new StringBuilder();
            sb.append(i);
            if (sb.length()==1){
                sb.insert(0,'0');
            }
            return sb;
        }

        public StringBuilder getResult() {
            return result;
        }

        public StringBuilder getDateTime() {
            return dateTime;
        }
    }
}
