package cz.cas.mbu.cydataseries;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;

/**
 * Note that data series are always mapped to columns to {@link CyNetwork#DEFAULT_ATTRS}, i.e. the mappings are shared by all networks
 * @author MBU
 *
 */
public interface DataSeriesMappingManager {
	static final Class<Integer> MAPPING_COLUMN_CLASS = Integer.class;
	
	void mapDataSeriesRowsToTableColumn(Class<? extends CyIdentifiable> targetClass, String columnName, DataSeries<?, ?> ds);
	void unmapTableColumn(Class<? extends CyIdentifiable> targetClass, String columnName);
	
	DataSeries<?,?> getMappedDataSeries(Class<? extends CyIdentifiable> targetClass, String columnName);

	<T extends DataSeries<?, ?>> T getMappedDataSeries(Class<? extends CyIdentifiable> targetClass, String columnName, Class<T> seriesClass);
	
	Map<Class<? extends CyIdentifiable>, Map<String, DataSeries<?,?>>> getAllMappings();
	List<MappingDescriptor<?>> getAllMappingDescriptors();
	<T extends DataSeries<?, ?>> List<MappingDescriptor<T>> getAllMappingDescriptors(Class<T> dataSeriesClass);
	
	<T extends DataSeries<?, ?>> List<MappingDescriptor<T>> getMappingDescriptorsForSeries(T dataSeries);
	
	Map<String, DataSeries<?,?>> getAllMappings(Class<? extends CyIdentifiable> targetClass);	
	<T extends DataSeries<?,?>> Map<String, T> getAllMappings(Class<? extends CyIdentifiable> targetClass, Class<T> dataSeriesClass);

	
	boolean isMappingsEmpty();
	
	/**
	 * Get all classes with at least one DS mapped.
	 * @return
	 */
	Collection<Class<? extends CyIdentifiable>> getTargetsWithMappedDataSeries();

}
