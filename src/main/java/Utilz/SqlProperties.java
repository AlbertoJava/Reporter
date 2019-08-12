package Utilz;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

;


public class SqlProperties implements Comparable {
    private HashMap<String, String> map = new HashMap();
    private String sourceFile = null;
    private Calendar startTime;
    private boolean isZip;
    //private Calendar timeToStart=null;
    /*
     *Reading props file with SQL query  from param
     * Make multi-line SQL-clause singl-line.
     * */

    public SqlProperties(boolean isZip) {
        this.isZip = isZip;
    }

    public static String toString(Calendar cdate) {
        if (cdate == null) return null;
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        return format.format(cdate.getTime());
    }

    public void setZip(boolean zip) {
        isZip = zip;
    }

    public HashMap<String, String> loadFromFile(InputStreamReader isr, String filePath) {
        StringBuilder stringB = getFileContextFromStream(isr);
        String[] params = stringB.toString().split(";");
        for (int i = 0; i < params.length; i++) {
            if (params[i].indexOf("=") == -1) continue;/*защита от строки без разделителя*/
            String[] pair = params[i].split("=", 2);
            map.put(pair[0].trim(), pair[1].trim());

        }
        if (map.containsKey("sql") & map.containsKey("date1") & map.containsKey("date2")) {
            String parammedSql = map.get("sql").replace("&&date1", map.get("date1"));
            parammedSql = parammedSql.replace("&&date2", map.get("date2"));
            map.put("sql", parammedSql);
        }
        map.put("sourceFile", filePath);
        sourceFile = getProperty("sourceFile");
        startTime = GregorianCalendar.getInstance();
        if (getProperty("period") != null) {
            addLong(startTime, Long.valueOf(getProperty("period")));
        } else {
            startTime = toCalendar(getProperty("date2"));
        }
        return map;
    }

    public HashMap<String, String> loadFromFile(File file) {
        try (FileReader fileReader = new FileReader(file)) {
            return loadFromFile(fileReader, file.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Calendar addLong(Calendar calendar, Long milliseconds) {
        int millsec = (int) (milliseconds % 1000);
        int sec = (int) ((milliseconds / 1000) % 60);
        int minutes = (int) ((milliseconds / 1000 / 60) % 60);
        int hours = (int) ((milliseconds / 1000 / 60 / 60) % 24);
        int days = (int) ((milliseconds / 1000 / 60 / 60 / 24) % 60);
        calendar.add(Calendar.MILLISECOND, millsec);
        calendar.add(Calendar.SECOND, sec);
        calendar.add(Calendar.MINUTE, minutes);
        calendar.add(Calendar.HOUR, hours);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar;
    }

    public HashMap<String, String> loadFromFile(String file) {
        File ffile = new File(file);
        sourceFile = file;
        return loadFromFile(ffile);
    }

    /*Print map collection*/
    public void printMap() {
        if (map == null || map.size() == 0) {
            System.out.println("Empty PropMap");
            return;
        }
        for (Map.Entry<String, String> pair :
                map.entrySet()) {
            System.out.println("Key: " + pair.getKey() + "; Value " + pair.getValue());
        }
    }

    public String getProperty(String key) {
        return map.get(key);
    }

    public void addProperty(String key, String value) {
        map.put(key, value);
    }

    private void printCalendar(Calendar cdate) {
        String sdate = cdate.get(Calendar.DAY_OF_MONTH) + "." + cdate.get(Calendar.MONTH) + "." + cdate.get(Calendar.YEAR);
        System.out.println(sdate);
    }

    public long calcSleepingTime() {
        Calendar currentMoment = new GregorianCalendar();
        long sleepingTime;
        if (getProperty("date2") == null) {
            sleepingTime = startTime.getTimeInMillis() - currentMoment.getTimeInMillis();
        } else {
            Calendar dateStart = toCalendar(getProperty("date2"));
            sleepingTime = dateStart.getTimeInMillis() - currentMoment.getTimeInMillis() - 360000;
        }
        return sleepingTime <= 0 ? 0 : sleepingTime;
    }

    public boolean updatePeriodinFile() {
        if (getProperty("reportPeriod") == null || getProperty("date1") == null || getProperty("date2") == null) {
            if (getProperty("period") == null) {
                return false;
            }
            addLong(startTime, Long.valueOf(getProperty("period")));
            return true;
        }
        Calendar date1 = toCalendar(getProperty("date1"));
        Calendar date2 = toCalendar(getProperty("date2"));
        switch (map.get("reportPeriod")) {
            case "day":
                date1.add(Calendar.DAY_OF_MONTH, 1);
                date2.add(Calendar.DAY_OF_MONTH, 1);
                break;
            case "week":
                date1.add(Calendar.DAY_OF_MONTH, 7);
                date2.add(Calendar.DAY_OF_MONTH, 7);
                break;
            case "month":
                date1.add(Calendar.MONTH, 1);
                date1.add(Calendar.MONTH, 1);
                break;
        }
        if (isZip) {
            return updatePropertiesZipFile("date1", toString(date1)) | updatePropertiesZipFile("date2", toString(date2));
        } else {
            return updatePropertiesFile("date1", toString(date1)) | updatePropertiesFile("date2", toString(date2));
        }
    }

    public Calendar toCalendar(String sdate) {
        String[] dateParts = sdate.split("\\.|/");
        if (dateParts.length != 3) return null;
        dateParts[0] = alignString(dateParts[0], 2, "0");
        dateParts[1] = alignString(dateParts[1], 2, "0");
        if (dateParts[2].length() == 2) {
            dateParts[2] = alignString(dateParts[2], 4, "20");
        }
        Calendar c = new GregorianCalendar();
        c.set(Integer.valueOf(dateParts[2]), Integer.valueOf(dateParts[1]) - 1, Integer.valueOf(dateParts[0]));
        return c;
    }

    /*
     * Метод выравнивает длину строки знаками sign до длины строки quantitySigns
     * */
    protected String alignString(String object, int quantitySigns, String sign) {
        if (object.length() >= quantitySigns) return object;
        StringBuilder result = new StringBuilder(object);
        while (result.length() < quantitySigns) {
            result.insert(0, sign);
        }
        return result.toString();
    }

    private boolean updatePropertiesFile(String nameProperty, String value) {
        //найти в файле свойство и переписать его значение и перезагрузить
        System.out.println("FileName sourceFile " + sourceFile);
        Path path = Paths.get(sourceFile);
        try {
            String contentOfFile = new String(Files.readAllBytes(path));
            contentOfFile = contentOfFile.replace(map.get(nameProperty), value);
            Files.write(path, contentOfFile.getBytes());
            map = loadFromFile(path.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean updatePropertiesZipFile(String nameProperty, String value) {
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
            e.printStackTrace();
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
            e.printStackTrace();
        } catch (ZipException e) {
            e.printStackTrace();
        }
        /*replace value in StringBuilder*/
        int start = stringB.indexOf(map.get(nameProperty));
        int end = start + map.get(nameProperty).length();
        stringB.replace(start, end, value);
        /*write StringBuolder to source in zip file */
        byte[] b = null;
        try (InputStream is = new ByteArrayInputStream(stringB.toString().getBytes("utf-8"))) {
            //b = stringB.toString().getBytes("UTF-8");
            ZipParameters zp = new ZipParameters();
            zp.setSourceExternalStream(true);
            zp.setFileNameInZip(sourceFile);
            zp.setPassword(BaseConstants.getInstance().getZipPsw());
            zipFile.addStream(is, zp);
            map.put(nameProperty, value);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ZipException e) {
            e.printStackTrace();
        }
        return true;
    }

    private StringBuilder getFileContextFromStream(InputStreamReader isr) {
        StringBuilder stringB = new StringBuilder();
        String line;
        try (BufferedReader reader = new BufferedReader(isr)) {
            while ((line = reader.readLine()) != null) {
                stringB.append(line);
                stringB.append("\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringB;
    }

    @Override
    public int compareTo(Object o) {
        if (calcSleepingTime() - ((SqlProperties) o).calcSleepingTime() > 0) return 1;
        if (calcSleepingTime() - ((SqlProperties) o).calcSleepingTime() <= 0) return -1;
        return 0;
    }
}
