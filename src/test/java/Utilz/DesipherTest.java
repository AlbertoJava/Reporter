package Utilz;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DesipherTest {
    DesipherTest desipherTest;

    @org.junit.Test
    public void decodeData() {
        assertEquals("e3x1x17", Desipher.encodeData("01/01/2019"));
        assertNotEquals("e3x1x17", Desipher.encodeData("01/02/2019"));
    }

    @org.junit.Test
    public void encodeData() {
        assertEquals("01.01.2019", Desipher.decodeData("e3x1x17"));
        assertNotEquals("01.02.2019", Desipher.decodeData("e3x1x17"));
    }
}