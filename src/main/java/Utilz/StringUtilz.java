package Utilz;

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
}
