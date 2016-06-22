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
import org.cytoscape.application.CyUserLog;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.session.events.SessionAboutToBeSavedEvent;
import org.cytoscape.session.events.SessionAboutToBeSavedListener;
import org.cytoscape.session.events.SessionLoadedEvent;
import org.cytoscape.session.events.SessionLoadedListener;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.apache.log4j.Logger;

import cz.cas.mbu.cytimeseries.DataSeries;
import cz.cas.mbu.cytimeseries.DataSeriesManager;
import cz.cas.mbu.cytimeseries.DataSeriesMappingManager;
import cz.cas.mbu.cytimeseries.DataSeriesStorageProvider;

public class DataSeriesStorageManager implements SessionAboutToBeSavedListener, SessionLoadedListener {
	private static final String SERIES_LIST_FILENAME = "_dataSeriesList.tsv";
	private static final String SERIES_NAME_COLUMN = "name";
	private static final String SERIES_SUID_COLUMN = "suid";
	private static final String SERIES_CLASS_COLUMN = "class";

	private static final String MAPPING_FILENAME = "_dataSeriesMappings.tsv";
	private static final String MAPPING_TARGET_CLASS_COLUMN = "targetClass";
	private static final String MAPPING_COLUMN_NAME_COLUMN = "columnName";
	private static final String MAPPING_SERIES_SUID_COLUMN = "seriesSUID";

	
	public static final CSVFormat CSV_FORMAT = CSVFormat.TDF;
	private final Logger userLogger = Logger.getLogger(CyUserLog.NAME); 
	private final Logger logger = Logger.getLogger(DataSeriesStorageManager.class); 
	
	private final DataSeriesManagerImpl dataSeriesManager;
	private final DataSeriesMappingManagerImpl mappingManager;
	
	private final ServiceTracker providerTracker;
	
	public DataSeriesStorageManager(BundleContext bc, DataSeriesManagerImpl dataSeriesManager, DataSeriesMappingManagerImpl mappingManager) {
		this.dataSeriesManager = dataSeriesManager;
		this.mappingManager = mappingManager;
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
		if(files == null || files.size() == 0)
		{
			return;
		}

		//First, find the list file
		Optional<File> listFile = files.stream()
				.filter((f) -> f.getName().equals(SERIES_LIST_FILENAME))
				.findAny();
		
		if(!listFile.isPresent())
		{
			if(!files.isEmpty())
			{
				userLogger.error("Could not find list of data series ('" + SERIES_LIST_FILENAME + "'), although some data series are probably present.");
			}
			
			return;
			
		}
		
		Map<String, DataSeriesStorageProvider> providerMap = getStorageProviders();
		
		Map<Long, DataSeries<?, ?>> oldSuidMapping = new HashMap<>();
		
		CSVFormat listFormat = CSV_FORMAT.withHeader();
		try (	CSVParser parser = new CSVParser(new FileReader(listFile.get()), listFormat) )
		{
			for(CSVRecord record : parser)
			{
				long oldsuid = Long.parseLong(record.get(SERIES_SUID_COLUMN));
				String name = record.get(SERIES_NAME_COLUMN);
				String className = record.get(SERIES_CLASS_COLUMN);
				DataSeriesStorageProvider provider = providerMap.get(className);
				if(provider == null)
				{
					userLogger.error("Could not find provider for DS name:" + name + " class: " + className);
				}
				else
				{
					try {
						//Now, find the series file
						String seriesFileName = getSeriesFileName(name, oldsuid);
						Optional<File> seriesFile = files.stream()
								.filter((f) -> f.getName().equals(seriesFileName))
								.findAny();
						
						if(seriesFile.isPresent())
						{
							DataSeries<?, ?> ds = provider.loadDataSeries(seriesFile.get(), name, oldsuid);
							oldSuidMapping.put(oldsuid, ds);
							dataSeriesManager.registerDataSeries(ds);
						}
						else
						{
							userLogger.error("Could not find session file for DS name:" + name + " file name: " + seriesFileName);							
						}
					}
					catch (RuntimeException ex)
					{
						userLogger.error("Error loading DS name:" + name, ex);													
					}
				}
			}			
		} 
		catch (IOException ex)
		{
			userLogger.error("Error reading DS list files", ex);
		}
		
		//Load mapping
		mappingManager.removeAllMappings();
		
		
		Optional<File> mappingFile = files.stream()
				.filter((f) -> f.getName().equals(MAPPING_FILENAME))
				.findAny();
		
		if(!mappingFile.isPresent())
		{
			userLogger.warn("Could not find data series mapping file ('" + MAPPING_FILENAME + "'), although some data series are present.");
		}
		else
		{
			CSVFormat mappingFormat = CSV_FORMAT.withHeader();
			try (CSVParser parser = new CSVParser(new FileReader(mappingFile.get()), mappingFormat))
			{
				for(CSVRecord record: parser)
				{
					try {
						String columnName = record.get(MAPPING_COLUMN_NAME_COLUMN);
						long seriesSuid = Long.parseLong(record.get(MAPPING_SERIES_SUID_COLUMN));
						if (!oldSuidMapping.containsKey(seriesSuid))
						{
							userLogger.error("Could not find DS with old SUID " + seriesSuid + " mentioned in mapping.");
							continue;
						}
						
						
						String targetClassName = record.get(MAPPING_TARGET_CLASS_COLUMN);
						Class<?> targetClassRaw = Class.forName(targetClassName);

						if(CyIdentifiable.class.isAssignableFrom(targetClassRaw))
						{
							Class<? extends CyIdentifiable> classCast = (Class<? extends CyIdentifiable>)targetClassRaw; 
							mappingManager.mapDataSeriesRowsToTableColumn(classCast, columnName, oldSuidMapping.get(seriesSuid));
						}
					}
					catch(ClassNotFoundException ex)
					{
						userLogger.error("Could not find target class for mapping.", ex);						
					}
				}
			}
			catch (IOException ex)
			{
				userLogger.error("Error reading data series mapping file.");
			}
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
			listPrinter.printRecord(SERIES_SUID_COLUMN, SERIES_NAME_COLUMN, SERIES_CLASS_COLUMN); //header
			
			for(DataSeries<?, ?> ds : dataSeriesManager.getAllDataSeries()) 
			{
				listPrinter.printRecord(ds.getSUID(), ds.getName(), ds.getClass().getName());
			};
		}
		catch (Exception ex)
		{
			userLogger.error("Error writing DS list file", ex);
		}
		
		Map<String, DataSeriesStorageProvider> providers = getStorageProviders();
		
		List<File> dsFiles = new ArrayList<>();
		dsFiles.add(listFile);
		
		for(DataSeries<?, ?> ds : dataSeriesManager.getAllDataSeries()) 
		{
			DataSeriesStorageProvider provider = providers.get(ds.getClass().getName());
			if(provider == null)
			{
				userLogger.error("Could not find provider for DS name:" + ds.getName() + " class: " + ds.getClass().getName());
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
					userLogger.error("Could not write DS name:" + ds.getName(), ex);
				}
			}
		};
		
		//Save mapping
		File mappingFile = new File(tmpDir, MAPPING_FILENAME);
		try (CSVPrinter mappingPrinter = new CSVPrinter(new FileWriter(mappingFile), CSV_FORMAT))
		{
			mappingPrinter.printRecord(MAPPING_TARGET_CLASS_COLUMN, MAPPING_COLUMN_NAME_COLUMN, MAPPING_SERIES_SUID_COLUMN);
			for(Class<? extends CyIdentifiable> targetClass : mappingManager.getTargetsWithMappedDataSeries())
			{
				for(Map.Entry<String, DataSeries<?, ?>> entry : mappingManager.getAllMappings(targetClass).entrySet())
				{
					mappingPrinter.printRecord(targetClass.getName(), entry.getKey(), entry.getValue().getSUID());
				}
			}
			dsFiles.add(mappingFile);
		} catch (IOException ex)
		{
			userLogger.error("Error writing mapping file.", ex);
		}		
		
		try {
			e.addAppFiles(CyActivator.APP_NAME_FOR_STORAGE, dsFiles);
		}
		catch (Exception ex)
		{
			userLogger.error("Error adding DS files to session.", ex);			
		}
	}	
}
