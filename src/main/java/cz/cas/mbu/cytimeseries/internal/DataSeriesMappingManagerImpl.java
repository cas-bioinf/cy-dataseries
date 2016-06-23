package cz.cas.mbu.cytimeseries.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.cytoscape.model.CyIdentifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import cz.cas.mbu.cytimeseries.DataSeries;
import cz.cas.mbu.cytimeseries.DataSeriesMappingManager;
import cz.cas.mbu.cytimeseries.DataSeriesMappingManager.MappingDescriptor;

public class DataSeriesMappingManagerImpl implements DataSeriesMappingManager{

	private final Logger logger = LoggerFactory.getLogger(DataSeriesMappingManagerImpl.class); 
	
	Map<Class<? extends CyIdentifiable>, Map<String, DataSeries<?,?>>> mappings;
	
	public  DataSeriesMappingManagerImpl()
	{
		mappings = new HashMap<>();
	}
	
	
	@Override
	public void mapDataSeriesRowsToTableColumn(Class<? extends CyIdentifiable> targetClass, String columnName,
			DataSeries<?, ?> ds) {
		Map<String, DataSeries<?, ?>> localMap = mappings.get(targetClass);
		if(localMap == null)
		{
			localMap = new HashMap<>();
			mappings.put(targetClass, localMap);
		}
		
		if(localMap.containsKey(columnName))
		{
			logger.warn("Remapping column '" + columnName +" for class " + targetClass.getSimpleName());			
		}
		localMap.put(columnName, ds);
	}

	@Override
	public void unmapTableColumn(Class<? extends CyIdentifiable> targetClass, String columnName) {
		Map<String, DataSeries<?, ?>> localMap = mappings.get(targetClass);
		if(localMap == null)
		{
			logger.warn("No mappings for " + targetClass.getSimpleName() + " exists. Cannot remove mapping for '" + columnName + "'");
			return;
		}
		if(!localMap.containsKey(columnName))
		{
			logger.warn("Mappings for column '" + columnName + "' for closs" + targetClass.getSimpleName() + " does not exist, cannot remove.");
			return;			
		}
		localMap.remove(columnName);
		
	}

	@Override
	public DataSeries<?, ?> getMappedDataSeries(Class<? extends CyIdentifiable> targetClass, String columnName) {
		Map<String, DataSeries<?, ?>> localMap = mappings.get(targetClass);
		if(localMap == null)
		{
			return null;
		}
		return localMap.get(columnName);
	}

	
	@Override
	public Map<Class<? extends CyIdentifiable>, Map<String, DataSeries<?,?>>> getAllMappings()
	{
		return mappings;
	}
	
	
	
	@Override
	public List<MappingDescriptor> getAllMappingDescriptors() {
		List<MappingDescriptor> descriptors = new ArrayList<>();
		mappings.entrySet().forEach(entry -> {
			entry.getValue().entrySet().forEach(perClassEntry ->
			{
				descriptors.add(new MappingDescriptor(entry.getKey(), perClassEntry.getKey(), perClassEntry.getValue()));
			});
		});
		return descriptors;
	}	


	@Override
	@SuppressWarnings("unchecked")
	public Map<String, DataSeries<?, ?>> getAllMappings(Class<? extends CyIdentifiable> targetClass) {
		Map<String, DataSeries<?, ?>> localMap = mappings.get(targetClass);
		if(localMap == null)
		{
			return Collections.EMPTY_MAP;
		}
		return Collections.unmodifiableMap(localMap);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends DataSeries<?, ?>> Map<String, T> getAllMappings(Class<? extends CyIdentifiable> targetClass,
			Class<T> dataSeriesClass) {
		Map<String, DataSeries<?, ?>> localMap = mappings.get(targetClass);
		if(localMap == null)
		{
			return Collections.EMPTY_MAP;
		}
		return  (Map<String, T>)Maps.filterEntries(localMap, e -> dataSeriesClass.isAssignableFrom(e.getValue().getClass()));
	}


	@Override
	public Collection<Class<? extends CyIdentifiable>> getTargetsWithMappedDataSeries() {
		return mappings.keySet();
	}
	
	
	public void removeAllMappings()
	{
		mappings.clear();
	}

}
