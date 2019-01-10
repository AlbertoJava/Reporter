package Utilz;

import org.junit.Before;
import org.junit.Test;
import sun.misc.Cleaner;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class SqlPropertiesTest {
    SqlProperties sqlProperties;

    @Before
    public void initTest(){
        sqlProperties = new SqlProperties(false);
    }


    @Test
    public void toCalendar() throws FileNotFoundException {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = new GregorianCalendar(2019,0,1);
        assertEquals(format.format(c.getTime()), format.format(sqlProperties.toCalendar("01.01.2019").getTime()));
    }
    @Test
    public void totoString() throws FileNotFoundException {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = new GregorianCalendar(2019,0,1);
        assertEquals("01/01/2019", sqlProperties.toString(c));
    }


    @Test
    public void alignString() {
        String test="11111";
        String result="0000011111";
        assertEquals(result,sqlProperties.alignString(test,10,"0"));
    }

    @Test
    public void   loadFromFile(){
        String sqlProp="date2 = 01/01/2019";
        String filePath = "TestFilePath";
        HashMap<String, String> result =null;

        InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(sqlProp.getBytes()));

        result = sqlProperties.loadFromFile(isr, filePath);
        System.out.println("startTime = " +sqlProperties.getStartTime().getTime());
        System.out.println(sqlProperties.toCalendar(result.get("date2")+ " 23:59:59").getTime());
        assertEquals(result.get("date2"), "01/01/2019");
        assertEquals(result.get("sourceFile"),filePath);
        Calendar c1 =sqlProperties.toCalendar(result.get("date2")+ " 23:59:59");
        c1.clear(Calendar.MILLISECOND);
        Calendar c2 = sqlProperties.getStartTime();
        c2.clear(Calendar.MILLISECOND);
        assertEquals(c1,c2);


    }

}