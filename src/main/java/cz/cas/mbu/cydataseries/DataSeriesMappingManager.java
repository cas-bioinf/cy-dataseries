package cz.cas.mbu.cydataseries;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

/**
 * 
 * @author MBU
 *
 */
public interface DataSeriesMappingManager {
	static final Class<Integer> MAPPING_COLUMN_CLASS = Integer.class;
	
	void mapDataSeriesRowsToTableColumn(CyNetwork network, Class<? extends CyIdentifiable> targetClass, String columnName, DataSeries<?, ?> ds);
	void unmapTableColumn(CyNetwork network, Class<? extends CyIdentifiable> targetClass, String columnName);
	void unmap(MappingDescriptor<? extends DataSeries<?,?>> descriptor);
	
	DataSeries<?,?> getMappedDataSeries(CyNetwork network, Class<? extends CyIdentifiable> targetClass, String columnName);

	<T extends DataSeries<?, ?>> T getMappedDataSeries(CyNetwork network, Class<? extends CyIdentifiable> targetClass, String columnName, Class<T> seriesClass);
	
	Map<CyNetwork, Map<Class<? extends CyIdentifiable>, Map<String, DataSeries<?,?>>>> getAllMappings();
	Map<Class<? extends CyIdentifiable>, Map<String, DataSeries<?,?>>> getAllMappings(CyNetwork network);
	
	List<MappingDescriptor<?>> getAllMappingDescriptors();
	<T extends DataSeries<?, ?>> List<MappingDescriptor<T>> getAllMappingDescriptors(Class<T> dataSeriesClass);
	
	<T extends DataSeries<?, ?>> List<MappingDescriptor<T>> getMappingDescriptorsForSeries(T dataSeries);
	
	Map<String, DataSeries<?,?>> getAllMappings(CyNetwork network, Class<? extends CyIdentifiable> targetClass);	
	<T extends DataSeries<?,?>> Map<String, T> getAllMappings(CyNetwork network, Class<? extends CyIdentifiable> targetClass, Class<T> dataSeriesClass);

	
	boolean isMappingsEmpty();
	
	/**
	 * Get all classes with at least one DS mapped.
	 * @return
	 */
	Collection<Class<? extends CyIdentifiable>> getTargetsWithMappedDataSeries(CyNetwork network);
	
	CyTable getMappingTable(CyNetwork network, Class<? extends CyIdentifiable> targetClass);

}
