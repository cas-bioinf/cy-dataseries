package cz.cas.mbu.cydataseries.internal;

import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.model.events.RowsSetListener;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.ServiceProperties;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.swing.GUITunableHandlerFactory;
import org.osgi.framework.BundleContext;

import cz.cas.mbu.cydataseries.DataSeriesListener;
import cz.cas.mbu.cydataseries.DataSeriesStorageProvider;
import cz.cas.mbu.cydataseries.dataimport.DataSeriesImportManager;
import cz.cas.mbu.cydataseries.dataimport.DataSeriesImportProvider;
import cz.cas.mbu.cydataseries.internal.data.TimeSeriesStorageProviderImpl;
import cz.cas.mbu.cydataseries.internal.dataimport.DataSeriesImportManagerImpl;
import cz.cas.mbu.cydataseries.internal.dataimport.ImportDataSeriesTaskFactory;
import cz.cas.mbu.cydataseries.internal.dataimport.ImportParametersGuiHandleFactory;
import cz.cas.mbu.cydataseries.internal.dataimport.TimeSeriesImportProviderImpl;
import cz.cas.mbu.cydataseries.internal.ui.DataSeriesPanel;
import cz.cas.mbu.cydataseries.internal.ui.DataSeriesVisualPanel;

public class CyActivator extends AbstractCyActivator {

	public static final String APP_NAME_FOR_STORAGE = CyActivator.class.getPackage().getName();
	
	@Override
	public void start(BundleContext bc) throws Exception {
		
		CyApplicationManager cyApplicationManager = getService(bc,CyApplicationManager.class);

		CyServiceRegistrar serviceRegistrar = getService(bc, CyServiceRegistrar.class);		
		
		DataSeriesManagerImpl dataSeriesManager = new DataSeriesManagerImpl(bc);
		registerAllServices(bc, dataSeriesManager, new Properties());
		
		

		DataSeriesMappingManagerImpl mappingManager = new DataSeriesMappingManagerImpl();
		registerAllServices(bc, mappingManager, new Properties());

		DataSeriesStorageManager storageManager = new DataSeriesStorageManager(bc, dataSeriesManager, mappingManager);
		registerAllServices(bc, storageManager, new Properties());
	
		DataSeriesStorageProvider timeSeriesProvider = new TimeSeriesStorageProviderImpl(); 
		registerService(bc, timeSeriesProvider, DataSeriesStorageProvider.class, new Properties());
		
		
		registerService(bc, new ImportParametersGuiHandleFactory(), GUITunableHandlerFactory.class, new Properties());
		
		registerService(bc, new TimeSeriesImportProviderImpl(), DataSeriesImportProvider.class, new Properties());
		
		DataSeriesImportManager importManager = new DataSeriesImportManagerImpl(bc);
		registerService(bc, importManager, DataSeriesImportManager.class, new Properties());
		
		Properties baseMenuProperties = new Properties();
		baseMenuProperties.setProperty(ServiceProperties.PREFERRED_MENU,"Apps.Data Series");
		baseMenuProperties.setProperty(ServiceProperties.IN_MENU_BAR,"true");
		
		Properties importProperties = new Properties();
		importProperties.putAll(baseMenuProperties);
		importProperties.setProperty(ServiceProperties.TITLE, "Import data Series");
		ImportDataSeriesTaskFactory importTaskFactory = new ImportDataSeriesTaskFactory(dataSeriesManager, importManager);
		registerService(bc, importTaskFactory, TaskFactory.class, importProperties);

		NetworkSelectedParameterPassingTaskFactory<MapColumnTask> mapColumnTaskFactory = new NetworkSelectedParameterPassingTaskFactory<>(MapColumnTask.class, cyApplicationManager, dataSeriesManager, mappingManager, cyApplicationManager);
		Properties mapProperties = new Properties();
		mapProperties.putAll(baseMenuProperties);
		mapProperties.setProperty(ServiceProperties.TITLE, "Map column to series");
		registerService(bc, mapColumnTaskFactory, TaskFactory.class, mapProperties);

		ParameterPassingTaskFactory<RemoveColumnMappingTask> removeMappingTaskFactory = new ParameterPassingTaskFactory<>(RemoveColumnMappingTask.class, mappingManager);
		Properties removeMappingProperties = new Properties();
		removeMappingProperties.putAll(baseMenuProperties);
		removeMappingProperties.setProperty(ServiceProperties.TITLE, "Remove column mapping");
		registerService(bc, removeMappingTaskFactory, TaskFactory.class, removeMappingProperties);

		ParameterPassingTaskFactory<ManageMappingsTask> manageMappingTaskFactory = new ParameterPassingTaskFactory<>(ManageMappingsTask.class, serviceRegistrar);
		Properties manageMappingProperties = new Properties();
		manageMappingProperties.putAll(baseMenuProperties);
		manageMappingProperties.setProperty(ServiceProperties.TITLE, "Manage column mappings");
		registerService(bc, manageMappingTaskFactory, TaskFactory.class, manageMappingProperties);
		
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
	}

}
