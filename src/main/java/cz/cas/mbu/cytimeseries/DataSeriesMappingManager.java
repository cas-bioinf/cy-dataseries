package cz.cas.mbu.cytimeseries;

import java.util.Map;

import org.cytoscape.model.CyIdentifiable;

public interface DataSeriesMappingManager {
	void mapDataSeriesRowsToTableColumn(Class<CyIdentifiable> targetClass, String columnName, DataSeries<?, ?> ds);
	void unmapTableColumn(Class<CyIdentifiable> targetClass, String columnName);
	
	DataSeries<?,?> getMappedDataSeries(Class<CyIdentifiable> targetClass, String columnName);
	
	Map<String, DataSeries<?,?>> getAllMappings(Class<CyIdentifiable> targetClass);	
	<T extends DataSeries<?,?>> Map<String, T> getAllMappings(Class<? extends CyIdentifiable> targetClass, Class<T> dataSeriesClass);	
}
