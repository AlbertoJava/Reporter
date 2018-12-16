package Utilz;


import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

    public  synchronized static void printRowToMonitor(String text){
            System.out.println(text);
    }
    public synchronized static  void printLineToMonitor(String text){
        System.out.print(text);
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
                e.printStackTrace();
                return false;
            }
        }
        StringBuilder sb = new StringBuilder();

        for (ConcurrentMap.Entry<String, ResultTable> pair:
             results.entrySet()) {
            sb.append("\r\n");
            sb.append("--------------------------------");
            sb.append(pair.getKey());
            sb.append(' ');
            sb.append(pair.getValue().getDateTime());
            sb.append("-----------------------------------\r\n");

            sb.append(pair.getValue().getResult());

            sb.append("--------------------------------");
            sb.append(pair.getKey());
            sb.append("-----------------------------------\r\n\r\n\r\n");
        }
/*
        try(FileOutputStream fos = new FileOutputStream(BaseConstants.getPath() + "\\" + sCurrentDate + ".txt","UTF-8"){
            //fout.write("");
            String secondString = new String(sb.toString().getBytes("windows-1251"),"UTF-8");
            while (fos.)

            fout.write (secondString);
        }
         catch (IOException e) {
            e.printStackTrace();
        }*/

        try {
            Files.write(path,
                        (sb.toString()).getBytes("windows-1251"),
                         StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
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
