package makenika.pl.splot;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Pair;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import splot.Sample1;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class Sample1Test extends ApplicationTestCase<Application> {
    private Sample1 sample1;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    public Sample1Test() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setOut(new PrintStream(outContent));
        sample1 = new Sample1(getContext());
        createApplication();
    }

    @Override
    public void tearDown() throws Exception {
        System.setOut(null);
        super.tearDown();
    }

    public void testEngineIsLoading() throws Exception {
        assertNotNull(sample1.getEngine());
    }

    public void testSplotModuleIsAccessible() throws Exception {
        final String version = new String(sample1.getSplotVersion());
        assertTrue(version.matches("^[0-9]+\\.[0-9]+\\.[0-9]+$"));
    }

    public void testMod1IsAccessible() throws Exception {
        final String name = new String(sample1.getMod1Name());
        assertEquals("mod1", name);
    }

    public void testSubMod1IsAccessible() throws Exception {
        final String name = new String(sample1.getSubmod1Name());
        assertEquals("mod1.submod1", name);
    }

    public void testDirectStringProperty() throws Exception {
        final String value = new String(sample1.getStringProperty());
        assertEquals("Hello world!", value);
    }

    public void testSetDirectStringProperty() throws Exception {
        final String newValue = "test value";
        sample1.setStringProperty(newValue.getBytes());
        final String value = new String(sample1.getStringProperty());
        assertEquals(newValue, value);
    }

    public void testDirectIntNumberProperty() throws Exception {
        assertEquals(58827.0, sample1.getIntProperty());
    }

    public void testSetDirectIntNumberProperty() throws Exception {
        sample1.setIntProperty(-82995.0);
        assertEquals(-82995.0, sample1.getIntProperty());
    }

    public void testDirectDoubleNumberProperty() throws Exception {
        assertEquals(1860.368, sample1.getDoubleProperty());
    }

    public void testSetDirectDoubleNumberProperty() throws Exception {
        sample1.setDoubleProperty(0.83221);
        assertEquals(0.83221, sample1.getDoubleProperty());
    }

    public void testDirectBooleanProperty() throws Exception {
        assertEquals(Boolean.TRUE, sample1.getBooleanProperty());
    }

    public void testSetDirectBooleanProperty() throws Exception {
        sample1.setBooleanProperty(false);
        assertEquals(Boolean.FALSE, sample1.getBooleanProperty());
    }

    public void testSimpleArray() throws Exception {
        final Sample1.Tarray array = sample1.getArray();
        assertNotNull(array);
        assertEquals(2, array.size());

        final byte[] b1 = array.get(1.0);
        assertNotNull(b1);
        final byte[] b2 = array.get(2.0);
        assertNotNull(b2);

        assertEquals("a", new String(b1));
        assertEquals("b", new String(b2));

        assertNull(array.get(3.0));
    }

    public void testHardArray() throws Exception {
        final Sample1.ThardArray array = sample1.getHardArray();
        assertNotNull(array);

        final byte[] b1 = array.get_1();
        assertNotNull(b1);
        final byte[] b2 = array.get_2();
        assertNotNull(b2);

        assertEquals("c", new String(b1));
        assertEquals("d", new String(b2));
    }

    public void testSetHardArray() throws Exception {
        final Sample1.ThardArray array = sample1.getHardArray();
        assertNotNull(array);

        array.set_1("y".getBytes());
        array.set_2("z".getBytes());

        final byte[] b1 = array.get_1();
        assertNotNull(b1);
        final byte[] b2 = array.get_2();
        assertNotNull(b2);

        assertEquals("y", new String(b1));
        assertEquals("z", new String(b2));
    }

    public void testNullableStringProperties() throws Exception {
        final byte[] b1 = sample1.getMaybeString1();
        assertNotNull(b1);
        final String s1 = new String(b1);
        assertEquals("here", s1);

        final byte[] b2 = sample1.getMaybeString2();
        assertNull(b2);
    }

    public void testDirectTableProperty() throws Exception {
        final Sample1.TtableProperty tableProperty = sample1.getTableProperty();
        assertNotNull(tableProperty);
        assertNotNull(tableProperty.getEngine());
        final String name = new String(tableProperty.getName());
        assertEquals("table property", name);

        final Sample1.TtableProperty.Tvalue tableValue = tableProperty.getValue();
        assertNotNull(tableValue);
        assertNotNull(tableValue.getEngine());
        final String innerName = new String(tableValue.getName());
        assertEquals("inner table", innerName);

        final Sample1.TtableProperty.Tvalue.TdeepValue deepValue = tableValue.getDeepValue();
        assertNotNull(deepValue);
        assertNotNull(deepValue.getEngine());
        final String deepName = new String(deepValue.getName());
        assertEquals("deep inner table", deepName);
    }

    public void testNullableTableInterface() throws Exception {
        final Sample1.Tfoo foo = sample1.getFoo();
        assertNotNull(foo);
        final byte[] b1 = foo.getFoo();
        assertNotNull(b1);
        assertEquals("abc", new String(b1));
    }

    public void testPrintFunction() throws Exception {
        sample1.printFunction();
    }

    public void testSumFunction() throws Exception {
        assertEquals(5.0, sample1.sum(2.0, 3.0));
        assertEquals(5.4, sample1.sum(2.2, 3.2));
        assertEquals(-8.7, sample1.sum(-5.1, -3.6));
    }

    public void testPairFunction() throws Exception {
        final Pair<byte[], Boolean> p = sample1.pairFunction("you".getBytes(), false);
        assertEquals("Hey you", new String(p.first));
        assertTrue(p.second);
    }

    public void testOptionalReturnValueFunction() throws Exception {
        final Double d1 = sample1.makeBigger(0.1);
        assertNull(d1);
        final Double d2 = sample1.makeBigger(8.0);
        assertEquals(80.0, d2);
    }

    public void testTableFunction() throws Exception {
        final Sample1.PTtableFunction1 param = new Sample1.PTtableFunction1(sample1.getEngine(), false);
        param.setVal(33.0);

        final Sample1.RTtableFunction1 ret = sample1.tableFunction(param);
        assertNotNull(ret);

        final byte[] val1 = ret.get(1.0);
        assertNull(val1);

        final byte[] val2 = ret.get(33.0);
        assertNotNull(val2);
        assertEquals("ok", new String(val2));
    }
}