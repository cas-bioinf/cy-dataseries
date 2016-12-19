package cz.cas.mbu.cydataseries.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import cz.cas.mbu.cydataseries.DataSeries;
import cz.cas.mbu.cydataseries.DataSeriesEvent;
import cz.cas.mbu.cydataseries.DataSeriesException;
import cz.cas.mbu.cydataseries.DataSeriesListener;
import cz.cas.mbu.cydataseries.DataSeriesMappingEvent;
import cz.cas.mbu.cydataseries.DataSeriesMappingEvent.EventType;
import cz.cas.mbu.cydataseries.DataSeriesMappingListener;
import cz.cas.mbu.cydataseries.DataSeriesMappingManager;
import cz.cas.mbu.cydataseries.MappingDescriptor;

public class DataSeriesMappingManagerImpl implements DataSeriesMappingManager{

	private final Logger logger = LoggerFactory.getLogger(DataSeriesMappingManagerImpl.class); 
	
	private final Map<CyNetwork, Map<Class<? extends CyIdentifiable>, Map<String, DataSeries<?,?>>>> mappings;
	
	private final ServiceTracker listenerTracker;

	public  DataSeriesMappingManagerImpl(BundleContext bc)
	{
		mappings = new HashMap<>();
		listenerTracker = new ServiceTracker(bc, DataSeriesMappingListener.class.getName(), null);
		listenerTracker.open();		
	}
	
	protected void fireEvent(DataSeriesMappingEvent event)
	{
		for(Object service : listenerTracker.getServices())
		{
			if(service instanceof DataSeriesMappingListener)
			{
				((DataSeriesMappingListener)service).handleEvent(event);				
			}
			else
			{
				logger.error("Listener is not of correct class");
			}
		}
	}
	
	
	@Override
	public void mapDataSeriesRowsToTableColumn(CyNetwork network, Class<? extends CyIdentifiable> targetClass, String columnName,
			DataSeries<?, ?> ds) {
		
		Map<Class<? extends CyIdentifiable>, Map<String, DataSeries<?, ?>>> networkMappings = mappings.get(network);
		if (networkMappings == null)
		{
			networkMappings = new HashMap<>();
			mappings.put(network, networkMappings);
		}
		
		Map<String, DataSeries<?, ?>> localMap = networkMappings.get(targetClass);
		if(localMap == null)
		{
			localMap = new HashMap<>();
			networkMappings.put(targetClass, localMap);
		}
		
		if(localMap.containsKey(columnName))
		{
			logger.warn("Remapping column '" + columnName +"' for network " + Utils.getNetworkName(network) + " class " + targetClass.getSimpleName());			
		}
		localMap.put(columnName, ds);
		
		fireEvent(new DataSeriesMappingEvent(this, EventType.MAPPING_ADDED, Collections.singletonList(new MappingDescriptor<DataSeries<?,?>>(network, targetClass, columnName, ds))));
	}

	@Override
	public void unmapTableColumn(CyNetwork network, Class<? extends CyIdentifiable> targetClass, String columnName) {		
		unmapTableColumnInternal(network, targetClass, columnName, true);
	}
	
	protected void unmapTableColumnInternal(CyNetwork network, Class<? extends CyIdentifiable> targetClass, String columnName, boolean fireEvent) {
		Map<Class<? extends CyIdentifiable>, Map<String, DataSeries<?, ?>>> networkMappings = mappings.get(network);
		if (networkMappings == null)
		{
			logger.warn("No mappings for network " + Utils.getNetworkName(network)  + " exists. Cannot remove mapping for '" + columnName + "'");
			return;			
		}
		
		Map<String, DataSeries<?, ?>> localMap = networkMappings.get(targetClass);
		if(localMap == null)
		{
			logger.warn("No mappings for " + targetClass.getSimpleName() + " exists. Cannot remove mapping for '" + columnName + "'");
			return;
		}
		if(!localMap.containsKey(columnName))
		{
			logger.warn("Mappings for column '" + columnName + "' for network " + Utils.getNetworkName(network)  + " and class " + targetClass.getSimpleName() + " does not exist, cannot remove.");
			return;			
		}
		DataSeries<?,?> ds = localMap.get(columnName); //store for event
		localMap.remove(columnName);

		if(fireEvent)
		{
			fireEvent(new DataSeriesMappingEvent(this, EventType.MAPPING_REMOVED, Collections.singletonList(new MappingDescriptor<DataSeries<?,?>>(network, targetClass, columnName, ds))));
		}		
	}

	@Override
	public DataSeries<?, ?> getMappedDataSeries(CyNetwork network, Class<? extends CyIdentifiable> targetClass, String columnName) {
		Map<Class<? extends CyIdentifiable>, Map<String, DataSeries<?, ?>>> networkMappings = mappings.get(network);
		if (networkMappings == null)
		{
			return null;
		}
		Map<String, DataSeries<?, ?>> localMap = networkMappings.get(targetClass);
		if(localMap == null)
		{
			return null;
		}
		return localMap.get(columnName);
	}
	
	
	@Override
	public <T extends DataSeries<?, ?>> T getMappedDataSeries(CyNetwork network, Class<? extends CyIdentifiable> targetClass,
			String columnName, Class<T> seriesClass) {
		DataSeries<?, ?> series = getMappedDataSeries(network, targetClass, columnName);
		if(series == null)
		{
			return null;
		}
		else if(seriesClass.isAssignableFrom(series.getClass()))
		{
			@SuppressWarnings("unchecked")
			T castSeries = (T)series;
			return (T)castSeries;
		}
		else
		{
			throw new DataSeriesException("Mapped data series " + series.getName() + " does not have expected class (" + seriesClass.getName() + ")");
		}
	}


	@Override
	public Map<CyNetwork, Map<Class<? extends CyIdentifiable>, Map<String, DataSeries<?,?>>>> getAllMappings()
	{
		return mappings;
	}
	
	
	
	@Override
	public <T extends DataSeries<?, ?>> List<MappingDescriptor<T>> getAllMappingDescriptors(Class<T> dataSeriesClass) {
		List<MappingDescriptor<T>> descriptors = new ArrayList<>();
		mappings.entrySet().forEach(entry -> {
			entry.getValue().entrySet().forEach(perNetworkEntry -> {			
				perNetworkEntry.getValue().entrySet().stream()
					.filter(perClassEntry -> dataSeriesClass.isAssignableFrom(perClassEntry.getValue().getClass())) //Filter by the given class
					.forEach(perClassEntry ->
					{
						@SuppressWarnings("unchecked")
						T dataSeries = (T)perClassEntry.getValue();
						descriptors.add(new MappingDescriptor<T>(entry.getKey(), perNetworkEntry.getKey(), perClassEntry.getKey(), dataSeries));
					});
			});
		});
		return descriptors;
	}




	@Override
	public List<MappingDescriptor<?>> getAllMappingDescriptors() {
		List<MappingDescriptor<?>> descriptors = new ArrayList<>();
		mappings.entrySet().forEach(entry -> {
			entry.getValue().entrySet().forEach(perNetworkEntry -> {			
				perNetworkEntry.getValue().entrySet().forEach(perClassEntry ->
				{
					descriptors.add(new MappingDescriptor<DataSeries<?,?>>(entry.getKey(), perNetworkEntry.getKey(), perClassEntry.getKey(), perClassEntry.getValue()));
				});
			});
		});
		return descriptors;
	}
		


	@Override
	public <T extends DataSeries<?, ?>> List<MappingDescriptor<T>> getMappingDescriptorsForSeries(T dataSeries) {
		List<MappingDescriptor<T>> descriptors = new ArrayList<>();
		mappings.entrySet().forEach(entry -> {
			entry.getValue().entrySet().forEach(perNetworkEntry -> {			
				perNetworkEntry.getValue().entrySet().forEach(perClassEntry ->
				{
					if (perClassEntry.getValue() == dataSeries) 
					{
						descriptors.add(new MappingDescriptor<T>(entry.getKey(), perNetworkEntry.getKey(), perClassEntry.getKey(), dataSeries));
					}
				});
			});
		});
		return descriptors;
	}

	

	@Override
	public void unmap(MappingDescriptor<? extends DataSeries<?, ?>> descriptor) {
		unmapTableColumn(descriptor.getNetwork(), descriptor.getTargetClass(), descriptor.getColumnName());
		
	}


	@Override
	@SuppressWarnings("unchecked")
	public Map<Class<? extends CyIdentifiable>, Map<String, DataSeries<?, ?>>> getAllMappings(CyNetwork network) {
		Map<Class<? extends CyIdentifiable>, Map<String, DataSeries<?, ?>>> networkMap = mappings.get(network);
		if(networkMap == null)
		{
			return Collections.EMPTY_MAP;
		}
		return networkMap;
	}


	@Override
	public CyTable getMappingTable(CyNetwork network, Class<? extends CyIdentifiable> targetClass) {
		return network.getTable(targetClass, CyNetwork.LOCAL_ATTRS);
	}


	@Override
	@SuppressWarnings("unchecked")
	public Map<String, DataSeries<?, ?>> getAllMappings(CyNetwork network, Class<? extends CyIdentifiable> targetClass) {
		Map<Class<? extends CyIdentifiable>, Map<String, DataSeries<?, ?>>> networkMap = getAllMappings(network);
		Map<String, DataSeries<?, ?>> localMap = networkMap.get(targetClass);
		if(localMap == null)
		{
			return Collections.EMPTY_MAP;
		}
		return Collections.unmodifiableMap(localMap);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends DataSeries<?, ?>> Map<String, T> getAllMappings(CyNetwork network, Class<? extends CyIdentifiable> targetClass,
			Class<T> dataSeriesClass) {
		Map<Class<? extends CyIdentifiable>, Map<String, DataSeries<?, ?>>> networkMap = mappings.get(network);
		if(networkMap == null)
		{
			return Collections.EMPTY_MAP;
		}
		Map<String, DataSeries<?, ?>> localMap = networkMap.get(targetClass);
		if(localMap == null)
		{
			return Collections.EMPTY_MAP;
		}
		return  (Map<String, T>)Maps.filterEntries(localMap, e -> dataSeriesClass.isAssignableFrom(e.getValue().getClass()));
	}

	

	@Override
	public boolean isMappingsEmpty() {
		for(Map<Class<? extends CyIdentifiable>, Map<String, DataSeries<?, ?>>> networkMapping : mappings.values())
		{
			for(Map<String, DataSeries<?,?>> classMapping: networkMapping.values())
			{
				if(!classMapping.isEmpty())
				{
					return false;
				}
			}
		}
		return true;
	}


	@Override
	public Collection<Class<? extends CyIdentifiable>> getTargetsWithMappedDataSeries(CyNetwork network) {
		Map<Class<? extends CyIdentifiable>, Map<String, DataSeries<?, ?>>> networkMapping = mappings.get(network);
		if(networkMapping == null)
		{
			return Collections.EMPTY_LIST;
		}
		return networkMapping.keySet();
	}
	
	
	public void removeAllMappings()
	{
		//store copy for event
		List<MappingDescriptor<? extends DataSeries<?,?>>> descriptors = getAllMappingDescriptors();
		
		mappings.clear();
		
		fireEvent(new DataSeriesMappingEvent(this, EventType.MAPPING_REMOVED, descriptors));
	}

}
