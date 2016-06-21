package cz.cas.mbu.cytimeseries;

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
	public void registerDataSeries(DataSeries<?,?> ds);

	public void unregisterDataSeries(DataSeries<?,?> ds);
	
}
