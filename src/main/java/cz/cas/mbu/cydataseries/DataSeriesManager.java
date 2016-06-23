package cz.cas.mbu.cydataseries;

import java.util.List;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

public interface DataSeriesManager {
	
	List<DataSeries<?,?>> getAllDataSeries();
	<T extends DataSeries<?, ?>> List<T> getDataSeriesByType(Class<T> type); 
	<T extends DataSeries<?, DATA>, DATA> List<T> getDataSeriesByDataType(Class<DATA> dataType); 
	<T extends DataSeries<INDEX, DATA>, INDEX, DATA> List<T> getDataSeriesByIndexAndDataType(Class<INDEX> indexType, Class<DATA> dataType); 
	
	/**
	 * Register a newly created DS, from know on, it will be saved and loaded.
	 * @param ds
	 */
	void registerDataSeries(DataSeries<?,?> ds);
	
	/**
	 * Register a list of data series (only one event is created)
	 * @param ds
	 */
	void registerDataSeries(List<? extends DataSeries<?, ?>> ds);
	
	void unregisterDataSeries(DataSeries<?,?> ds);
	
}
