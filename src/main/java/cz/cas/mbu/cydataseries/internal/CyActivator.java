package cz.cas.mbu.cydataseries.internal;

import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.events.RowsSetListener;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.ServiceProperties;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.swing.GUITunableHandlerFactory;
import org.osgi.framework.BundleContext;

import cz.cas.mbu.cydataseries.DataSeriesFactory;
import cz.cas.mbu.cydataseries.DataSeriesListener;
import cz.cas.mbu.cydataseries.DataSeriesMappingListener;
import cz.cas.mbu.cydataseries.DataSeriesPublicTasks;
import cz.cas.mbu.cydataseries.DataSeriesStorageManager;
import cz.cas.mbu.cydataseries.DataSeriesStorageProvider;
import cz.cas.mbu.cydataseries.MappingManipulationService;
import cz.cas.mbu.cydataseries.SmoothingService;
import cz.cas.mbu.cydataseries.dataimport.DataSeriesImportManager;
import cz.cas.mbu.cydataseries.dataimport.DataSeriesImportProvider;
import cz.cas.mbu.cydataseries.internal.data.NamedDoubleDataSeriesStorageProviderImpl;
import cz.cas.mbu.cydataseries.internal.data.TimeSeriesStorageProviderImpl;
import cz.cas.mbu.cydataseries.internal.dataimport.DataSeriesImportManagerImpl;
import cz.cas.mbu.cydataseries.internal.dataimport.ImportDataSeriesTaskFactory;
import cz.cas.mbu.cydataseries.internal.dataimport.ImportSoftFileTaskFactory;
import cz.cas.mbu.cydataseries.internal.dataimport.NamedDoubleDataSeriesImportProviderImpl;
import cz.cas.mbu.cydataseries.internal.dataimport.SoftFileImportParametersGUIHandlerFactory;
import cz.cas.mbu.cydataseries.internal.dataimport.TabularFileImportParametersGUIHandlerFactory;
import cz.cas.mbu.cydataseries.internal.dataimport.TimeSeriesImportProviderImpl;
import cz.cas.mbu.cydataseries.internal.smoothing.SmoothingServiceImpl;
import cz.cas.mbu.cydataseries.internal.tasks.ExponentiateDataSeriesTask;
import cz.cas.mbu.cydataseries.internal.tasks.ExponentiateDataSeriesTaskFactory;
import cz.cas.mbu.cydataseries.internal.tasks.ExportDataSeriesTaskFactory;
import cz.cas.mbu.cydataseries.internal.tasks.ManageMappingsTaskFactory;
import cz.cas.mbu.cydataseries.internal.tasks.MapColumnTaskFactory;
import cz.cas.mbu.cydataseries.internal.tasks.RemoveColumnMappingTaskFactory;
import cz.cas.mbu.cydataseries.internal.tasks.RemoveDataSeriesTaskFactory;
import cz.cas.mbu.cydataseries.internal.tasks.SmoothDataSeriesTaskFactory;
import cz.cas.mbu.cydataseries.internal.tasks.SmoothInteractiveShowUITaskFactory;
import cz.cas.mbu.cydataseries.internal.ui.DataSeriesPanel;
import cz.cas.mbu.cydataseries.internal.ui.DataSeriesVisualPanel;

/** Entry point for bundle. */
public class CyActivator extends AbstractCyActivator {

	private static final String DEFAULT_MENU = "Apps.Data Series";
	public static final String APP_NAME_FOR_STORAGE = CyActivator.class.getPackage().getName();

	private void registerMenuItem(BundleContext bc, String title, TaskFactory taskFactory)
	{
		registerMenuItem(bc, DEFAULT_MENU, title, taskFactory);
	}
	
	private void registerMenuItem(BundleContext bc, String menu, String title, TaskFactory taskFactory)
	{
		Properties properties = new Properties();
		properties.setProperty(ServiceProperties.IN_MENU_BAR, "true");
		properties.setProperty(ServiceProperties.PREFERRED_MENU, menu);
		properties.setProperty(ServiceProperties.TITLE, title);
		
		registerService(bc, taskFactory, TaskFactory.class, properties);		
	}
	
	
	
	@Override
	public void start(BundleContext bc) throws Exception {
		
		CyApplicationManager cyApplicationManager = getService(bc,CyApplicationManager.class);

		CyServiceRegistrar serviceRegistrar = getService(bc, CyServiceRegistrar.class);		

		DataSeriesMappingManagerImpl mappingManager = new DataSeriesMappingManagerImpl(bc);
		registerAllServices(bc, mappingManager, new Properties());

		DataSeriesManagerImpl dataSeriesManager = new DataSeriesManagerImpl(bc, mappingManager);
		registerAllServices(bc, dataSeriesManager, new Properties());

		DataSeriesStorageManager storageManager = new DataSeriesStorageManagerImpl(bc, serviceRegistrar.getService(CyNetworkManager.class), dataSeriesManager, mappingManager);
		registerAllServices(bc, storageManager, new Properties());
	
		registerService(bc, new TimeSeriesStorageProviderImpl(), DataSeriesStorageProvider.class, new Properties());
		registerService(bc, new NamedDoubleDataSeriesStorageProviderImpl(), DataSeriesStorageProvider.class, new Properties());

		SmoothingService smoothingService = new SmoothingServiceImpl(); 
		registerService(bc, smoothingService, SmoothingService.class, new Properties());
		
		registerService(bc, new TabularFileImportParametersGUIHandlerFactory(), GUITunableHandlerFactory.class, new Properties());
		registerService(bc, new SoftFileImportParametersGUIHandlerFactory(), GUITunableHandlerFactory.class, new Properties());
		
		registerService(bc, new TimeSeriesImportProviderImpl(), DataSeriesImportProvider.class, new Properties());
		registerService(bc, new NamedDoubleDataSeriesImportProviderImpl(), DataSeriesImportProvider.class, new Properties());

		registerService(bc, new DataSeriesFactoryImpl(), DataSeriesFactory.class, new Properties());

		registerService(bc, new MappingManipulationServiceImpl(serviceRegistrar), MappingManipulationService.class, new Properties());
		
		DataSeriesImportManager importManager = new DataSeriesImportManagerImpl(bc);
		registerService(bc, importManager, DataSeriesImportManager.class, new Properties());
		
		
		ImportDataSeriesTaskFactory importTaskFactory = new ImportDataSeriesTaskFactory(serviceRegistrar);
		registerMenuItem(bc, "File.Import.Data Series", "From tabular file (.csv,.tsv, etc.) ...", importTaskFactory);
		ImportSoftFileTaskFactory importSoftTaskFactory = new ImportSoftFileTaskFactory(serviceRegistrar);
		registerMenuItem(bc, "File.Import.Data Series", "From SOFT file...", importSoftTaskFactory);

		registerMenuItem(bc, "File.Export", "Data Series...", new ExportDataSeriesTaskFactory(dataSeriesManager, storageManager));

		registerMenuItem(bc, "Remove Data Series", new RemoveDataSeriesTaskFactory(dataSeriesManager));

		MapColumnTaskFactory mapColumnTaskFactory = new MapColumnTaskFactory(serviceRegistrar);
		//registerMenuItem(bc, "Map Column to Series", mapColumnTaskFactory);

		//registerMenuItem(bc, "Remove Column Mapping", new RemoveColumnMappingTaskFactory(mappingManager));

		registerMenuItem(bc, "Manage Column Mappings", new ManageMappingsTaskFactory(serviceRegistrar));

		registerMenuItem(bc, DEFAULT_MENU + ".Advanced smoothing", "Linear kernel", new SmoothDataSeriesTaskFactory(serviceRegistrar));

		registerMenuItem(bc, "Exponentiate Data Series", new ExponentiateDataSeriesTaskFactory(dataSeriesManager));
		
		SmoothInteractiveShowUITaskFactory smoothInteractiveTaskFactory = new SmoothInteractiveShowUITaskFactory(serviceRegistrar);
		registerMenuItem(bc, "Interactive smoothing", smoothInteractiveTaskFactory);
		
		/*
		Properties modifyProperties = new Properties(baseMenuProperties);
		modifyProperties.setProperty(ServiceProperties.TITLE, "Add Time Series...");
		registerService(bc, addTaskFactory, TaskFactory.class, addProperties);
		*/
		DataSeriesPanel panel = new DataSeriesPanel(serviceRegistrar);
		registerService(bc, panel, CytoPanelComponent.class, new Properties());
		registerService(bc, panel, DataSeriesListener.class, new Properties());
		
		DataSeriesVisualPanel visualPanel = new DataSeriesVisualPanel(cyApplicationManager, dataSeriesManager, mappingManager);
		registerService(bc, visualPanel, CytoPanelComponent.class, new Properties());
		registerService(bc, visualPanel, RowsSetListener.class, new Properties());
		registerService(bc, visualPanel, DataSeriesListener.class, new Properties());
		registerService(bc, visualPanel, DataSeriesMappingListener.class, new Properties());
		
		registerService(bc, new DataSeriesPublicTasksImpl(importTaskFactory, importSoftTaskFactory, mapColumnTaskFactory, smoothInteractiveTaskFactory), DataSeriesPublicTasks.class, new Properties());
	}

}
