package cz.cas.mbu.cytimeseries.internal;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.service.util.CyServiceRegistrar;
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

public class DataSeriesStorageManager implements SessionAboutToBeSavedListener, SessionLoadedListener {
	private static final String SERIES_LIST_FILENAME = "_dataSeriesList.tsv";
	private static final String NAME_COLUMN = "name";
	private static final String SUID_COLUMN = "suid";
	private static final String CLASS_COLUMN = "class";

	public static final CSVFormat CSV_FORMAT = CSVFormat.TDF;
	private final Logger logger = LoggerFactory.getLogger(DataSeriesStorageManager.class); 
	
	private final DataSeriesManagerImpl dataSeriesManager;
	
	private final ServiceTracker providerTracker;
	
	public DataSeriesStorageManager(BundleContext bc, DataSeriesManagerImpl dataSeriesManager) {
		this.dataSeriesManager = dataSeriesManager;
		providerTracker = new ServiceTracker(bc, DataSeriesStorageProvider.class.getName(), null);
		providerTracker.open();
	}
	
	/**
	 * Gathers all registered services of type {@link DataSeriesStorageProvider}.
	 * @return
	 */
	private Map<String, DataSeriesStorageProvider> getStorageProviders()
	{
		Object[] providerObjects = providerTracker.getServices();
		Map<String, DataSeriesStorageProvider> providers = new HashMap<>();
		
		if(providerObjects == null)
		{
			return providers;
		}
		
		for(Object obj : providerObjects)
		{
			try
			{
				DataSeriesStorageProvider p = (DataSeriesStorageProvider)obj;
				providers.put(p.getProvidedClass().getName(), p);				
			}
			catch(ClassCastException ex)
			{
				logger.error("Provider service cannot be cast to correct type.", ex);				
			}
		}
		return providers;
	}	
	
	private String getSeriesFileName(String name, long suid)
	{
		return "ds_" + suid + "_" + name + ".tsv";
	}
	
	@Override
	public void handleEvent(SessionLoadedEvent e) {
		
		dataSeriesManager.removeAllDataSeries();
		
		if (e.getLoadedSession().getAppFileListMap() == null || e.getLoadedSession().getAppFileListMap().size() ==0){
			return;
		}
		
		List<File> files = e.getLoadedSession().getAppFileListMap().get(CyActivator.APP_NAME_FOR_STORAGE);

		//First, find the list file
		Optional<File> listFile = files.stream()
				.filter((f) -> f.getName().equals(SERIES_LIST_FILENAME))
				.findAny();
		
		if(!listFile.isPresent())
		{
			if(!files.isEmpty())
			{
				logger.error("Could not find list of data series ('" + SERIES_LIST_FILENAME + "'), although some data series are probably present.");
			}
			
			return;
			
		}
		
		Map<String, DataSeriesStorageProvider> providerMap = getStorageProviders();
		
		CSVFormat listFormat = CSV_FORMAT.withHeader();
		try (	CSVParser parser = new CSVParser(new FileReader(listFile.get()), listFormat) )
		{
			for(CSVRecord record : parser)
			{
				long suid = Long.parseLong(record.get(SUID_COLUMN));
				String name = record.get(NAME_COLUMN);
				String className = record.get(CLASS_COLUMN);
				DataSeriesStorageProvider provider = providerMap.get(className);
				if(provider == null)
				{
					logger.error("Could not find provider for DS name:" + name + " class: " + className);
				}
				else
				{
					try {
						//Now, find the series file
						String seriesFileName = getSeriesFileName(name, suid);
						Optional<File> seriesFile = files.stream()
								.filter((f) -> f.getName().equals(seriesFileName))
								.findAny();
						
						if(seriesFile.isPresent())
						{
							DataSeries<?, ?> ds = provider.loadDataSeries(seriesFile.get(), name, suid);
							dataSeriesManager.registerDataSeries(ds);
						}
						else
						{
							logger.error("Could not find session file for DS name:" + name + " file name: " + seriesFileName);							
						}
					}
					catch (RuntimeException ex)
					{
						logger.error("Error loading DS name:" + name, ex);													
					}
				}
			}			
		} 
		catch (IOException ex)
		{
			logger.error("Error reading DS list files", ex);
		}
	}





	@Override
	public void handleEvent(SessionAboutToBeSavedEvent e) {
		if(dataSeriesManager.getAllDataSeries().isEmpty())
		{
			return;
		}
		
		String tmpDir = System.getProperty("java.io.tmpdir");
		File listFile = new File(tmpDir, SERIES_LIST_FILENAME);		
		
		try (CSVPrinter listPrinter = new CSVPrinter(new FileWriter(listFile), CSV_FORMAT))
		{
			listPrinter.printRecord(SUID_COLUMN, NAME_COLUMN, CLASS_COLUMN); //header
			
			for(DataSeries<?, ?> ds : dataSeriesManager.getAllDataSeries()) 
			{
				listPrinter.printRecord(ds.getSUID(), ds.getName(), ds.getClass().getName());
			};
		}
		catch (IOException ex)
		{
			logger.error("Error writing DS list file", ex);
		}
		
		Map<String, DataSeriesStorageProvider> providers = getStorageProviders();
		
		List<File> dsFiles = new ArrayList<>();
		dsFiles.add(listFile);
		
		for(DataSeries<?, ?> ds : dataSeriesManager.getAllDataSeries()) 
		{
			DataSeriesStorageProvider provider = providers.get(ds.getClass().getName());
			if(provider == null)
			{
				logger.error("Could not find provider for DS name:" + ds.getName() + " class: " + ds.getClass().getName());
			}
			else
			{
				File dsFile = new File(tmpDir, getSeriesFileName(ds.getName(), ds.getSUID()));
				try {
					provider.saveDataSeries(ds, dsFile);
					dsFiles.add(dsFile);
				}
				catch(IOException ex)
				{
					logger.error("Could not write DS name:" + ds.getName(), ex);
				}
			}
		};
		
		try {
			e.addAppFiles(CyActivator.APP_NAME_FOR_STORAGE, dsFiles);
		}
		catch (Exception ex)
		{
			logger.error("Error adding DS files to session.", ex);			
		}
	}	
}
