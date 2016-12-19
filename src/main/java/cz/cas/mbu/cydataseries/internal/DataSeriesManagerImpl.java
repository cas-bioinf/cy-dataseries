package cz.cas.mbu.cydataseries.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.cytoscape.session.events.SessionAboutToBeSavedEvent;
import org.cytoscape.session.events.SessionAboutToBeSavedListener;
import org.cytoscape.session.events.SessionLoadedEvent;
import org.cytoscape.session.events.SessionLoadedListener;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cas.mbu.cydataseries.DataSeries;
import cz.cas.mbu.cydataseries.DataSeriesEvent;
import cz.cas.mbu.cydataseries.DataSeriesListener;
import cz.cas.mbu.cydataseries.DataSeriesManager;
import cz.cas.mbu.cydataseries.DataSeriesMappingManager;
import cz.cas.mbu.cydataseries.DataSeriesStorageProvider;

public class DataSeriesManagerImpl implements DataSeriesManager {

	
	private final Logger logger = LoggerFactory.getLogger(DataSeriesManagerImpl.class); 
	
	private final ServiceTracker listenerTracker;
	
	private final DataSeriesMappingManager mappingManager;
	
	private final List<DataSeries<?, ?>> dataSeries;
	
	public DataSeriesManagerImpl(BundleContext bc, DataSeriesMappingManager mappingManager) {
		super();
		this.mappingManager = mappingManager;
		dataSeries = new ArrayList<>();
		listenerTracker = new ServiceTracker(bc, DataSeriesListener.class.getName(), null);
		listenerTracker.open();		
	}
	
	
	@Override
	public List<DataSeries<?, ?>> getAllDataSeries() {
		return dataSeries;
	}


	public void removeAllDataSeries()
	{
		List<DataSeries<?, ?>> seriesCopy = new ArrayList<>(dataSeries);
		dataSeries.clear();
		fireEvent(new DataSeriesEvent(this, DataSeriesEvent.EventType.DS_REMOVED, seriesCopy));
	}



	@Override
	@SuppressWarnings("unchecked")
	public <T extends DataSeries<?, ?>> List<T> getDataSeriesByType(Class<T> type) {		
		return dataSeries.stream()
				.filter(ds -> type.isAssignableFrom(ds.getClass()))
				.map(ds -> ((T)ds))
				.collect(Collectors.toList());		
	}





	@Override
	@SuppressWarnings("unchecked")
	public <T extends DataSeries<?, DATA>, DATA> List<T> getDataSeriesByDataType(Class<DATA> dataType) {
		return dataSeries.stream()
				.filter(ds -> dataType.isAssignableFrom(ds.getDataClass()))
				.map(ds -> ((T)ds))
				.collect(Collectors.toList());		
	}





	@Override
	@SuppressWarnings("unchecked")
	public <T extends DataSeries<INDEX, DATA>, INDEX, DATA> List<T> getDataSeriesByIndexAndDataType(
			Class<INDEX> indexType, Class<DATA> dataType) {
		return dataSeries.stream()
				.filter(ds -> dataType.isAssignableFrom(ds.getDataClass()) && indexType.isAssignableFrom(ds.getIndexClass()))
				.map(ds -> ((T)ds))
				.collect(Collectors.toList());		
	}

	
	protected void fireEvent(DataSeriesEvent event)
	{
		for(Object service : listenerTracker.getServices())
		{
			if(service instanceof DataSeriesListener)
			{
				((DataSeriesListener)service).handleEvent(event);				
			}
			else
			{
				logger.error("Listener is not of correct class");
			}
		}
	}

	@Override
	public void registerDataSeries(DataSeries<?, ?> ds) {
		if(ds == null)
		{
			throw new NullPointerException("Data series cannot be null");
		}
		dataSeries.add(ds);
		fireEvent(new DataSeriesEvent(this, DataSeriesEvent.EventType.DS_ADDED, Collections.singletonList(ds)));
	}

	@Override
	public void registerDataSeries(List<? extends DataSeries<?, ?>> ds) {
		if(ds == null)
		{
			throw new NullPointerException("Data series cannot be null");
		}
		dataSeries.addAll(ds);
		fireEvent(new DataSeriesEvent(this, DataSeriesEvent.EventType.DS_ADDED, ds));
	}

	@Override
	public void unregisterDataSeries(DataSeries<?, ?> ds) {
		dataSeries.remove(ds);
		mappingManager.getAllMappingDescriptors().stream()
			.filter( descriptor -> (descriptor.getDataSeries() == ds)) //get all descriptors that match the removed DS
			.forEach( descriptor -> { 
				mappingManager.unmap(descriptor); //and unmap them
			});
		fireEvent(new DataSeriesEvent(this, DataSeriesEvent.EventType.DS_REMOVED, Collections.singletonList(ds)));
	}



	
	
}
