package Utilz;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class StringUtilz {
    /*
     * Метод выравнивает длину строки знаками sign до длины строки quantitySigns
     * */
    public static String alignString (String object, int quantitySigns, String sign){
        if (object.length()>=quantitySigns) return object;
        StringBuilder result = new StringBuilder(object);
        while (result.length()<quantitySigns) {
            result.insert(0,sign);
        }
        return result.toString();
    }
    public static String alignStringAfter (String object, int quantitySigns, String sign){
        if (object.length()>=quantitySigns) return object;
        StringBuilder result = new StringBuilder(object);
        while (result.length()<quantitySigns) {
            result.append(sign);
        }
        return result.toString();
    }

    public static String toString(Calendar cdate){
        if (cdate==null) return null;
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        return format.format(cdate.getTime());
    }
}
