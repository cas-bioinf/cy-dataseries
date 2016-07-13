package cz.cas.mbu.cydataseries.internal.data;

import cz.cas.mbu.cydataseries.TimeSeries;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;


public class TimeSeriesImplTest {
    private TimeSeries timeSeries;

    @org.junit.Before
    public void setUp() throws Exception {
        // test data consisting of 4x3 matrix, i.e. 4 rows, 3 columns
        Long suid = new Long(123);
        String name = "test-series";
        int[] rowIDs = {0, 1, 2, 3};

        List<String> rowNames = new LinkedList<String>();
        rowNames.add("r0");
        rowNames.add("r1");
        rowNames.add("r2");
        rowNames.add("r3");

        double[] indexArray = {10.0, 11.0, 12.0};
        double[][] dataArray = { {0.0,1.0,2.0}, {3.0,4.0,5.0}, {6.0,7.0,8.0}, {9.0,10.0,11.0} };

        timeSeries = new TimeSeriesImpl(suid, name, rowIDs, rowNames, indexArray, dataArray);
    }

    @org.junit.After
    public void tearDown() throws Exception {
        timeSeries = null;
    }

    @org.junit.Test
    public void getIndexArray() throws Exception {
        double[] indexArray = timeSeries.getIndexArray();
        assertNotNull(indexArray);
        assertEquals(3, indexArray.length);
        assertEquals(11.0, indexArray[1], 1E-6);
    }

    @org.junit.Test
    public void getDataArray() throws Exception {
        double[][] dataArray = timeSeries.getDataArray();
        assertNotNull(dataArray);
        assertEquals(4, dataArray.length); // length checks for rows
        assertEquals(1.0, dataArray[0][1], 1E-6);
        assertEquals(11.0, dataArray[3][2], 1E-6);
    }

}