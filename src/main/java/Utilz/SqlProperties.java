package Utilz;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;;
import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static Utilz.Printer.printRowToMonitor;


public class SqlProperties implements Comparable<SqlProperties>{
    private HashMap<String,String> map = new HashMap();
    private String sourceFile =null;
    private Calendar startTime;
    private boolean isZip;
    private boolean isRunning;
    private int localPeriod=0;
    private  static  final Object lock = new Object();

    //private Calendar timeToStart=null;
    /*
    *Reading props file with SQL query  from param
    * Make multi-line SQL-clause singl-line.
    * */

    public SqlProperties(boolean isZip) {
        this.isZip = isZip;
        this.isRunning=false;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public void setZip(boolean zip) {
        isZip = zip;
    }

    public synchronized HashMap<String, String> loadFromFile(InputStreamReader isr, String filePath) {
         StringBuilder stringB = getFileContextFromStream(isr);

        String [] params = stringB.toString().split(";");
        for (int i=0;i<params.length;i++){
            if (params[i].indexOf("=")==-1) continue;/*защита от строки без разделителя*/
            String [] pair = params[i].split("=",2);
            map.put(pair[0].trim(),pair[1].trim());

        }
        if (map.containsKey("sql") & map.containsKey("date1")& map.containsKey("date2")){
            String parammedSql = map.get("sql").replace("&&date1",map.get("date1"));
            parammedSql=parammedSql.replace("&&date2",map.get("date2"));
            map.put("sql",parammedSql);
        }

        if (getProperty("period")!=null && localPeriod>0){
            map.put("period", String.valueOf(localPeriod));
        }
        //updateTimeToStart();

        map.put("sourceFile", filePath);
        sourceFile=getProperty("sourceFile");


        if (getProperty("date2")==null /*& getProperty("period")!=null*/) {
            startTime=GregorianCalendar.getInstance();
            addLong(startTime, Long.valueOf(getProperty("period")));
        }
        else {

            startTime=toCalendar(getProperty("date2")+ " 23:59:59");
        }
        return map;
    }

    public HashMap<String, String> loadFromFile(File file){
        //try (FileReader fileReader =new FileReader(file,"utf-8")){
        try (InputStreamReader fileReader =new InputStreamReader(new FileInputStream(file),"utf-8")){
            return loadFromFile(fileReader,file.getPath());
        } catch (FileNotFoundException e) {
            Printer.printLog(e);
        } catch (IOException e) {
            Printer.printLog(e);
        }
        return null;
    }

    private Calendar addLong (Calendar calendar, Long milliseconds){
        int millsec = (int) (milliseconds%1000);
        int sec = (int) ((milliseconds/1000)%60);
        int minutes = (int) ((milliseconds/1000/60)%60);
        int hours = (int) ((milliseconds/1000/60/60)%24);
        int days = (int) ((milliseconds/1000/60/60/24)%60);
        calendar.add(Calendar.MILLISECOND,millsec);
        calendar.add(Calendar.SECOND,sec);
        calendar.add(Calendar.MINUTE,minutes);
        calendar.add(Calendar.HOUR,hours);
        calendar.add(Calendar.DAY_OF_MONTH,days);
        return calendar;
    }


    /*Print map collection*/
    public void printMap(){
        if (map == null || map.size()==0) {
            Printer.printLog("Empty PropMap");
            return;
        }
        for (Map.Entry<String, String> pair:
             map.entrySet()) {
            printRowToMonitor("Key: " + pair.getKey() + "; Value " + pair.getValue() );
        }
    }

    public String getProperty(String key){
        return map.get(key);
    }
    public void  addProperty (String key, String value){
        map.put(key,value);
    }


    private void printCalendar(Calendar cdate) {
        String sdate=cdate.get(Calendar.DAY_OF_MONTH)+"."+cdate.get(Calendar.MONTH)+"."+cdate.get(Calendar.YEAR);
        printRowToMonitor(sdate);
    }

    public long calcSleepingTime (){
        Calendar currentMoment = Calendar.getInstance();/*текущее время*/
        long sleepingTime = startTime.getTimeInMillis() -currentMoment.getTimeInMillis();
        return sleepingTime <= 0 ? 0 : sleepingTime;
    }



    public boolean updatePeriodinFile(){
        if (getProperty("reportPeriod")==null || getProperty("date1")==null|| getProperty("date2")==null){
            if (getProperty("period")==null){
                return false;
            }
            startTime = GregorianCalendar.getInstance();
            addLong(startTime, Long.valueOf (getProperty("period")));
            return true;
        }

        Calendar date1= toCalendar(getProperty("date1"));
        Calendar date2= toCalendar(getProperty("date2"));
        switch (map.get("reportPeriod")){
            case "day":
                date1.add(Calendar.DAY_OF_MONTH,1);
                date2.add(Calendar.DAY_OF_MONTH,1);
                break;
            case "week":
                date1.add(Calendar.DAY_OF_MONTH,7);
                date2.add(Calendar.DAY_OF_MONTH,7);
                break;
            case "month":
                date1.add(Calendar.MONTH,1);
                date2.add(Calendar.MONTH,1);
                break;
        }
        //startTime=date2;

        startTime=toCalendar(StringUtilz.toString(date2)+ " 23:59:59");
        if (isZip) {

            boolean r= updatePropertiesZipFile("date2", StringUtilz.toString(date2)) | updatePropertiesZipFile("date1", StringUtilz.toString(date1));
            return r;
        }
        else{
            boolean r= updatePropertiesFile("date2", StringUtilz.toString(date2)) | updatePropertiesFile("date1", StringUtilz.toString(date1));
            return r;
        }
    }

    public Calendar toCalendar(String sdate){
        String [] dateParts = sdate.split("\\.|/| |:");
        Calendar c = new GregorianCalendar();
        if (dateParts.length!=3 & dateParts.length!=6) return null;
        dateParts[0] = alignString(dateParts[0], 2, "0");
        dateParts[1] = alignString(dateParts[1], 2, "0");
        if (dateParts[2].length() == 2) {
            dateParts[2] = alignString(dateParts[2], 4, "20");
        }
        c.set(Integer.valueOf(dateParts[2]), Integer.valueOf(dateParts[1]) - 1, Integer.valueOf(dateParts[0]));
        if (dateParts.length==6){
            c.set(Calendar.HOUR_OF_DAY,Integer.valueOf(dateParts[3]));
            c.set(Calendar.MINUTE,Integer.valueOf(dateParts[4]));
            c.set(Calendar.SECOND,Integer.valueOf(dateParts[5]));
        }
        printRowToMonitor(sdate);
        //printRowToMonitor(c.getTime());
        return c;
    }

    /*
     * Метод выравнивает длину строки знаками sign до длины строки quantitySigns
     * */
    protected String alignString (String object, int quantitySigns, String sign){
        if (object.length()>=quantitySigns) return object;
        StringBuilder result = new StringBuilder(object);
        while (result.length()<quantitySigns) {
            result.insert(0,sign);
        }
        return result.toString();
    }

    private boolean updatePropertiesFile(String nameProperty, String value){
        //найти в файле свойство и переписать его значение и перезагрузить
        Printer.printLog(getProperty("description") + ", updatePropertiesFile. FileName sourceFile " + sourceFile);
        Path path = Paths.get(sourceFile);
        try {
            String contentOfFile = new String(Files.readAllBytes(path));
            contentOfFile=contentOfFile.replace(map.get(nameProperty),value);
            Files.write(path,contentOfFile.getBytes());
            map=loadFromFile(path.toFile());
        } catch (IOException e) {
            Printer.printLog(e);
        }
        return true;
    }

    private  boolean  updatePropertiesZipFile(String nameProperty, String value)  {
        synchronized (lock) {
            /*read source in zip file to StringBuilder*/
            String zipFilePath = BaseConstants.getInstance().getZipFileSQL();
            FileHeader fHeader = null;
            ZipFile zipFile = null;
            try {
                zipFile = new ZipFile(zipFilePath);
                if (zipFile.isEncrypted()) {
                    zipFile.setPassword(BaseConstants.getInstance().getZipPsw());
                }
                fHeader = zipFile.getFileHeader(sourceFile);
            } catch (ZipException e) {
                Printer.printLog(e);
                try {
                    wait(10000);
                } catch (InterruptedException e1) {
                    Printer.printLog(e1);
                }
            }
            StringBuilder stringB = null;
            try (InputStreamReader isr = new InputStreamReader(
                    zipFile.getInputStream(fHeader), "utf-8"
            )
            ) {
                isr.getEncoding();
                stringB = getFileContextFromStream(isr);
                zipFile.removeFile(fHeader);
            } catch (IOException e) {
                Printer.printLog(e);
            } catch (ZipException e) {
                Printer.printLog(e);
            }
            /*replace value in StringBuilder*/
            int start = stringB.indexOf(map.get(nameProperty));
            int end = start + map.get(nameProperty).length();
            stringB.replace(start, end, value);
            //map.put(nameProperty,value);
            /*write StringBulder to source in zip file */
            map.put(nameProperty, value);
            byte[] b = null;
            try (InputStream is = new ByteArrayInputStream(stringB.toString().getBytes("utf-8"))) {
                //b = stringB.toString().getBytes("UTF-8");
                ZipParameters zp = new ZipParameters();
                zp.setSourceExternalStream(true);
                zp.setFileNameInZip(sourceFile);
                zp.setPassword(BaseConstants.getInstance().getZipPsw());
                zipFile.addStream(is, zp);
                //map.put(nameProperty,value);
            } catch (IOException e) {
                Printer.printLog(e);
            } catch (ZipException e) {
                Printer.printLog(e);            }
        }
        return true;
    }

    private StringBuilder getFileContextFromStream(InputStreamReader isr) {
        StringBuilder stringB = new StringBuilder();
        String line = null;
        try (BufferedReader reader = new BufferedReader(isr)) {
            while ((line = reader.readLine())!=null) {
                //stringB.append(" ");
                stringB.append(line);
                stringB.append("\r\n");

                //stringB.append(" ");
            }
        } catch (IOException e) {
            Printer.printLog(e);
        }
        return stringB;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    @Override
    public int compareTo(SqlProperties o) {
        if (isRunning() & ! o.isRunning() ){
            return 1;
        }
        else if (isRunning() & o.isRunning() ){
            return 0;
        }
        else if (!isRunning & o.isRunning){
            return -1;
        }
        if (calcSleepingTime() - o.calcSleepingTime() >0) return 1;
        if (calcSleepingTime() - o.calcSleepingTime() <0) return -1;
        return 0;
    }
}
