package cz.cas.mbu.cydataseries.internal;

import java.util.List;

import org.cytoscape.model.SUIDFactory;

import cz.cas.mbu.cydataseries.DataSeriesFactory;
import cz.cas.mbu.cydataseries.TimeSeries;
import cz.cas.mbu.cydataseries.internal.data.TimeSeriesImpl;

public class DataSeriesFactoryImpl implements DataSeriesFactory {

	@Override
	public TimeSeries createTimeSeries(String name, List<String> rowNames, double[] timePoints, double[][] data) {
		int[] rowIDs = new int[data.length];
		for(int row = 0; row < rowIDs.length; row++)
		{
			rowIDs[row] = row;
		}
		return new TimeSeriesImpl(SUIDFactory.getNextSUID(), name, rowIDs, rowNames, timePoints, data);
	}
	
}
