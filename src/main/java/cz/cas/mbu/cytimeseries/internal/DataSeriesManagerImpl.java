package cz.cas.mbu.cytimeseries.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

	
	private static final Logger logger = LoggerFactory.getLogger(DataSeriesManagerImpl.class); 
	
	
	private final List<DataSeries<?, ?>> dataSeries;
	
	public DataSeriesManagerImpl() {
		super();
		dataSeries = new ArrayList<>();
	}
	
	
	@Override
	public List<DataSeries<?, ?>> getAllDataSeries() {
		// TODO Auto-generated method stub
		return null;
	}


	public void removeAllDataSeries()
	{
		dataSeries.clear();
	}



	@Override
	public <T extends DataSeries<?, ?>> List<T> getDataSeriesByType(Class<T> type) {
		// TODO Auto-generated method stub
		return null;
	}





	@Override
	public <T extends DataSeries<?, DATA>, DATA> List<T> getDataSeriesByDataType(Class<DATA> dataType) {
		// TODO Auto-generated method stub
		return null;
	}





	@Override
	public <T extends DataSeries<INDEX, DATA>, INDEX, DATA> List<T> getDataSeriesByIndexAndDataType(
			Class<INDEX> indexType, Class<DATA> dataType) {
		// TODO Auto-generated method stub
		return null;
	}



	
	
}
