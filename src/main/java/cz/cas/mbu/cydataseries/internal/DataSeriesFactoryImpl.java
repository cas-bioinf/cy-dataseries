package cz.cas.mbu.cydataseries.internal;

import java.util.List;

import org.cytoscape.model.SUIDFactory;

import cz.cas.mbu.cydataseries.DataSeriesFactory;
import cz.cas.mbu.cydataseries.NamedDoubleDataSeries;
import cz.cas.mbu.cydataseries.TimeSeries;
import cz.cas.mbu.cydataseries.internal.data.NamedDoubleDataSeriesImpl;
import cz.cas.mbu.cydataseries.internal.data.TimeSeriesImpl;

/**
 * Implementation of {@link DataSeriesFactory}, check there for method descriptions.
 * @author MBU
 *
 */
public class DataSeriesFactoryImpl implements DataSeriesFactory {

	private int[] createRowIDs(int count)
	{
		int[] rowIDs = new int[count];
		for(int row = 0; row < count; row++)
		{
			rowIDs[row] = row;
		}		
		return rowIDs;
	}
	
	@Override
	public TimeSeries createTimeSeries(String name, List<String> rowNames, double[] timePoints, double[][] data) {
		return new TimeSeriesImpl(SUIDFactory.getNextSUID(), name, createRowIDs(rowNames.size()), rowNames, timePoints, data);
	}

	@Override
	public NamedDoubleDataSeries createNamedDoubleDataSeries(String name, List<String> rowNames,
			List<String> columnNames, double[][] data) {
		return new NamedDoubleDataSeriesImpl(SUIDFactory.getNextSUID(), name, createRowIDs(rowNames.size()), rowNames, columnNames, data);
	}

	
}
