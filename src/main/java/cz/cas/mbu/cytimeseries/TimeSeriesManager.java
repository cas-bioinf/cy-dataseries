package cz.cas.mbu.cytimeseries;

import java.util.List;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

public interface TimeSeriesManager {
	public static final String TIME_SERIES_TABLE_CONTEXT = "cz.cas.mbu.cytimeseries";
	
	/**
	 * Returns the time series table to let you, for example, add additional time series attributes as columns.
	 * DO NOT delete columns you have not created from the table. Also DO NOT delete rows from the table, use {@link #deleteTimeSeries(TimeSeries)}
	 * instead. 
	 * @param network
	 * @return the time series table or null, if such a table does not exist (e.g. when no time series have been created so far for the network)
	 */
	public CyTable getTimeSeriesTable(CyNetwork network);
	
	/**
	 * If no time series table is associated with the given network, this method creates one.
	 * Usually you would not need to call this, as the table is automatically created with the first time series.
	 * @param network
	 * @return the time series table
	 */
	public CyTable ensureTimeSeriesTableExists(CyNetwork network);
	
	public <TARGET_TYPE extends CyIdentifiable> TimeSeries<TARGET_TYPE> createTimeSeries(CyNetwork network, Class<TARGET_TYPE> targetClass);
	public <TARGET_TYPE extends CyIdentifiable> List<TimeSeries<TARGET_TYPE>> getAllTimeSeries(CyNetwork network, Class<TARGET_TYPE> targetClass);	
	public <TARGET_TYPE extends CyIdentifiable> void deleteTimeSeries(TimeSeries<TARGET_TYPE> series);
}
