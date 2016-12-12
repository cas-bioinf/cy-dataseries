package cz.cas.mbu.cydataseries;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

/**
 * Service to map series to columns in node/edge tables in specific networks.
 * @author MBU
 *
 */
public interface DataSeriesMappingManager {
	/**
	 * Data type of columns used for mapping.
	 */
	static final Class<Integer> MAPPING_COLUMN_CLASS = Integer.class;

	/**
	 * Create a mapping.
	 * @param network
	 * @param targetClass
	 * @param columnName
	 * @param ds
	 */
	void mapDataSeriesRowsToTableColumn(CyNetwork network, Class<? extends CyIdentifiable> targetClass, String columnName, DataSeries<?, ?> ds);
	
	/**
	 * Remove a mapping by explicitly givinig the mapping parameters
	 * @param network
	 * @param targetClass
	 * @param columnName
	 */
	void unmapTableColumn(CyNetwork network, Class<? extends CyIdentifiable> targetClass, String columnName);
	
	/**
	 * Remove a mapping by giving a {@link MappingDescriptor}.
	 * @param descriptor
	 */
	void unmap(MappingDescriptor<? extends DataSeries<?,?>> descriptor);
	
	/**
	 * Get a series (if any) mapped to the given column in the given table in the given network.
	 * @param network
	 * @param targetClass
	 * @param columnName
	 * @return the series or null if no such series exists
	 */
	DataSeries<?,?> getMappedDataSeries(CyNetwork network, Class<? extends CyIdentifiable> targetClass, String columnName);

	/**
	 * Get a series (if any) of the given class, mapped to the given column in the given table in the given network. 
	 * @param network
	 * @param targetClass
	 * @param columnName
	 * @param seriesClass the class of the series
	 * @return the series or null if no such series exists or if the series is of a different class.
	 */
	<T extends DataSeries<?, ?>> T getMappedDataSeries(CyNetwork network, Class<? extends CyIdentifiable> targetClass, String columnName, Class<T> seriesClass);
	
	/**
	 * Get all mappings.
	 * @return map - first key: the network, second: entity type (CyNode/CyEdge), third: column name
	 */
	Map<CyNetwork, Map<Class<? extends CyIdentifiable>, Map<String, DataSeries<?,?>>>> getAllMappings();
	
	/**
	 * Get all mappings for the given network.
	 * @param network
	 * @return map - first key: entity type (CyNode/CyEdge), second: column name	 
	 * */	
	Map<Class<? extends CyIdentifiable>, Map<String, DataSeries<?,?>>> getAllMappings(CyNetwork network);

	/**
	 * Get all mappings of series for the given network and entity type.
	 * @param network
	 * @param targetClass the entity the series is associated with (CyNode/CyEdge)
	 * @return map - column name to series	 
	 * */	
	Map<String, DataSeries<?,?>> getAllMappings(CyNetwork network, Class<? extends CyIdentifiable> targetClass);	
	
	/**
	 * Get all mappings of series of a given class for the given network and entity type.
	 * @param network
	 * @param targetClass the entity the series is associated with (CyNode/CyEdge)
	 * @param dataSeriesClass limit the results to series of this class 
	 * @return map - column name to series	 
	 * */	
	<T extends DataSeries<?,?>> Map<String, T> getAllMappings(CyNetwork network, Class<? extends CyIdentifiable> targetClass, Class<T> dataSeriesClass);
	
	/**
	 * Get all mapping as descriptors
	 * @return
	 */
	List<MappingDescriptor<?>> getAllMappingDescriptors();
	
	/**
	 * Get all descriptors mapping a given series class.
	 * @param dataSeriesClass
	 * @return
	 */
	<T extends DataSeries<?, ?>> List<MappingDescriptor<T>> getAllMappingDescriptors(Class<T> dataSeriesClass);
	
	/**
	 * Get all mappings of a given series as descriptors
	 * @param dataSeries
	 * @return
	 */
	<T extends DataSeries<?, ?>> List<MappingDescriptor<T>> getMappingDescriptorsForSeries(T dataSeries);
	

	/**
	 * True, if no mappings are present.
	 * @return
	 */
	boolean isMappingsEmpty();
	
	/**
	 * Get all classes (CyNode/CyEdge) with at least one DS mapped.
	 * @return
	 */
	Collection<Class<? extends CyIdentifiable>> getTargetsWithMappedDataSeries(CyNetwork network);
	
	/**
	 * Get the table that should contain the columns the series map to 
	 * @param network target network
	 * @param targetClass entity type (CyNode/CyEdge)
	 * @return table, where the columns corresponding to a mapping should be found
	 */
	CyTable getMappingTable(CyNetwork network, Class<? extends CyIdentifiable> targetClass);

}
