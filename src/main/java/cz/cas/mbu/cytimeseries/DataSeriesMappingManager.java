package cz.cas.mbu.cytimeseries;

import java.util.Collection;
import java.util.Map;

import org.cytoscape.model.CyIdentifiable;

public interface DataSeriesMappingManager {
	void mapDataSeriesRowsToTableColumn(Class<? extends CyIdentifiable> targetClass, String columnName, DataSeries<?, ?> ds);
	void unmapTableColumn(Class<? extends CyIdentifiable> targetClass, String columnName);
	
	DataSeries<?,?> getMappedDataSeries(Class<? extends CyIdentifiable> targetClass, String columnName);
	
	Map<String, DataSeries<?,?>> getAllMappings(Class<? extends CyIdentifiable> targetClass);	
	<T extends DataSeries<?,?>> Map<String, T> getAllMappings(Class<? extends CyIdentifiable> targetClass, Class<T> dataSeriesClass);
	
	/**
	 * Get all classes with at least one DS mapped.
	 * @return
	 */
	Collection<Class<? extends CyIdentifiable>> getTargetsWithMappedDataSeries();
	
}
