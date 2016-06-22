package cz.cas.mbu.cytimeseries.internal;

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

import cz.cas.mbu.cytimeseries.DataSeries;
import cz.cas.mbu.cytimeseries.DataSeriesManager;
import cz.cas.mbu.cytimeseries.DataSeriesStorageProvider;

public class DataSeriesManagerImpl implements DataSeriesManager {

	
	private final Logger logger = LoggerFactory.getLogger(DataSeriesManagerImpl.class); 
	
	
	private final List<DataSeries<?, ?>> dataSeries;
	
	public DataSeriesManagerImpl() {
		super();
		dataSeries = new ArrayList<>();
	}
	
	
	@Override
	public List<DataSeries<?, ?>> getAllDataSeries() {
		return dataSeries;
	}


	public void removeAllDataSeries()
	{
		dataSeries.clear();
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


	@Override
	public void registerDataSeries(DataSeries<?, ?> ds) {
		if(ds == null)
		{
			throw new NullPointerException("Data series cannot be null");
		}
		dataSeries.add(ds);		
	}


	@Override
	public void unregisterDataSeries(DataSeries<?, ?> ds) {
		dataSeries.remove(ds);
	}



	
	
}
