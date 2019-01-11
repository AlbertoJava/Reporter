package Utilz;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Desipher {

    public static void main(String[] args) {
        System.out.println(decodeData("e3x1x17"));
        System.out.println(encodeData("01/03/2019"));
    }
    public static String decodeData(String s){
        String decodedData = s.substring(s.length()-1,s.length()) +
                s.substring(0,s.length()-1);
        String [] dataParts =decodedData.split("x");
        try {
            int year_1 = Integer.parseInt(dataParts[0], 16);
            int month_1 = Integer.parseInt(dataParts[1], 16);
            int day_1 = Integer.parseInt(dataParts[2], 16);
            Calendar c = new GregorianCalendar(year_1, month_1 - 1, day_1);
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
            String currentDate = formatter.format(c.getTime());
            return currentDate;
        }
        catch (NumberFormatException e){
            return null;
        }

    }

    public static String  encodeData(String s){
        StringBuilder data= new StringBuilder(s);
        Calendar c= new GregorianCalendar();
        c.set(
                Integer.valueOf(data.substring(6,10)),
                Integer.valueOf(data.substring(3,5)),
                Integer.valueOf(data.substring(0,2))
        );
        int year = c.get(Calendar.YEAR);
        int month = c.get (Calendar.MONTH);
        int day = c.get (Calendar.DAY_OF_MONTH);
         String offsetedData =  Integer.toString(year,16) +'x' +Integer.toString(month,16) +'x' + Integer.toString(day,16);
        offsetedData = offsetedData.substring(1,offsetedData.length()) + offsetedData.substring(0,1);
        return offsetedData;
    }

}
