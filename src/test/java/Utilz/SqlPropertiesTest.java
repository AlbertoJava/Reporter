package Utilz;

import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;

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
}