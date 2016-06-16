package cz.cas.mbu.cytimeseries.internal;

import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.events.RowsSetListener;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.work.ServiceProperties;
import org.cytoscape.work.TaskFactory;
import org.osgi.framework.BundleContext;

import cz.cas.mbu.cytimeseries.DataSeriesMappingManager;
import cz.cas.mbu.cytimeseries.DataSeriesStorageProvider;
import cz.cas.mbu.cytimeseries.internal.data.TimeSeriesStorageProviderImpl;

public class CyActivator extends AbstractCyActivator {

	public static final String APP_NAME_FOR_STORAGE = CyActivator.class.getPackage().getName();
	
	@Override
	public void start(BundleContext bc) throws Exception {
		
		CyNetworkManager cyNetworkManager = getService(bc,CyNetworkManager.class);
		CyApplicationManager cyApplicationManager = getService(bc,CyApplicationManager.class);
		CyTableManager cyTableManager = getService(bc, CyTableManager.class);
		CyTableFactory cyTableFactory = getService(bc, CyTableFactory.class);
		CyNetworkTableManager cyNetworkTableManager = getService(bc, CyNetworkTableManager.class);

		DataSeriesManagerImpl dataSeriesManager = new DataSeriesManagerImpl();
		registerAllServices(bc, dataSeriesManager, new Properties());
		
		DataSeriesStorageManager storageManager = new DataSeriesStorageManager(bc, dataSeriesManager);
		registerAllServices(bc, storageManager, new Properties());
		

		DataSeriesMappingManager mappingManager = new DataSeriesMappingManagerImpl();
		registerAllServices(bc, mappingManager, new Properties());
	
		DataSeriesStorageProvider timeSeriesProvider = new TimeSeriesStorageProviderImpl(); 
		registerService(bc, timeSeriesProvider, DataSeriesStorageProvider.class, new Properties());
		
		ParameterPassingTaskFactory<AddTimeSeriesTask> addTaskFactory = new ParameterPassingTaskFactory<>(AddTimeSeriesTask.class, dataSeriesManager, timeSeriesProvider);
		
		Properties baseMenuProperties = new Properties();
		baseMenuProperties.setProperty(ServiceProperties.PREFERRED_MENU,"Apps.Time Series");
		baseMenuProperties.setProperty(ServiceProperties.IN_MENU_BAR,"true");
		
		Properties addProperties = new Properties(baseMenuProperties);
		addProperties.setProperty(ServiceProperties.TITLE, "Add Time Series to Network");
		addProperties.setProperty(ServiceProperties.PREFERRED_MENU,"Apps.Time Series");
		addProperties.setProperty(ServiceProperties.IN_MENU_BAR,"true");
		registerService(bc, addTaskFactory, TaskFactory.class, addProperties);

		/*
		Properties modifyProperties = new Properties(baseMenuProperties);
		modifyProperties.setProperty(ServiceProperties.TITLE, "Add Time Series...");
		registerService(bc, addTaskFactory, TaskFactory.class, addProperties);
		*/
		DataSeriesVisualPanel panel = new DataSeriesVisualPanel(cyApplicationManager, dataSeriesManager, mappingManager);
		registerService(bc, panel, CytoPanelComponent.class, new Properties());
		registerService(bc, panel, RowsSetListener.class, new Properties());
	}

}
