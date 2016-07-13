package cz.cas.mbu.cydataseries.internal;

import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.model.events.RowsSetListener;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.ServiceProperties;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.swing.GUITunableHandlerFactory;
import org.osgi.framework.BundleContext;

import cz.cas.mbu.cydataseries.DataSeriesFactory;
import cz.cas.mbu.cydataseries.DataSeriesListener;
import cz.cas.mbu.cydataseries.DataSeriesStorageManager;
import cz.cas.mbu.cydataseries.DataSeriesStorageProvider;
import cz.cas.mbu.cydataseries.dataimport.DataSeriesImportManager;
import cz.cas.mbu.cydataseries.dataimport.DataSeriesImportProvider;
import cz.cas.mbu.cydataseries.internal.data.TimeSeriesStorageProviderImpl;
import cz.cas.mbu.cydataseries.internal.dataimport.DataSeriesImportManagerImpl;
import cz.cas.mbu.cydataseries.internal.dataimport.ImportDataSeriesTaskFactory;
import cz.cas.mbu.cydataseries.internal.dataimport.ImportParametersGuiHandleFactory;
import cz.cas.mbu.cydataseries.internal.dataimport.TimeSeriesImportProviderImpl;
import cz.cas.mbu.cydataseries.internal.tasks.ExportDataSeriesTaskFactory;
import cz.cas.mbu.cydataseries.internal.tasks.ManageMappingsTask;
import cz.cas.mbu.cydataseries.internal.tasks.NetworkSelectedParameterPassingTaskFactory;
import cz.cas.mbu.cydataseries.internal.tasks.ParameterPassingTaskFactory;
import cz.cas.mbu.cydataseries.internal.tasks.RemoveColumnMappingTask;
import cz.cas.mbu.cydataseries.internal.tasks.RemoveColumnMappingTaskFactory;
import cz.cas.mbu.cydataseries.internal.tasks.RemoveDataSeriesTask;
import cz.cas.mbu.cydataseries.internal.tasks.RemoveDataSeriesTaskFactory;
import cz.cas.mbu.cydataseries.internal.ui.DataSeriesPanel;
import cz.cas.mbu.cydataseries.internal.ui.DataSeriesVisualPanel;

/** Entry point for bundle. */
public class CyActivator extends AbstractCyActivator {

	public static final String APP_NAME_FOR_STORAGE = CyActivator.class.getPackage().getName();
	
	@Override
	public void start(BundleContext bc) throws Exception {
		
		CyApplicationManager cyApplicationManager = getService(bc,CyApplicationManager.class);

		CyServiceRegistrar serviceRegistrar = getService(bc, CyServiceRegistrar.class);		

		DataSeriesMappingManagerImpl mappingManager = new DataSeriesMappingManagerImpl();
		registerAllServices(bc, mappingManager, new Properties());

		DataSeriesManagerImpl dataSeriesManager = new DataSeriesManagerImpl(bc, mappingManager);
		registerAllServices(bc, dataSeriesManager, new Properties());

		DataSeriesStorageManager storageManager = new DataSeriesStorageManagerImpl(bc, dataSeriesManager, mappingManager);
		registerAllServices(bc, storageManager, new Properties());
	
		DataSeriesStorageProvider timeSeriesProvider = new TimeSeriesStorageProviderImpl(); 
		registerService(bc, timeSeriesProvider, DataSeriesStorageProvider.class, new Properties());
		
		
		registerService(bc, new ImportParametersGuiHandleFactory(), GUITunableHandlerFactory.class, new Properties());
		
		registerService(bc, new TimeSeriesImportProviderImpl(), DataSeriesImportProvider.class, new Properties());

		registerService(bc, new DataSeriesFactoryImpl(), DataSeriesFactory.class, new Properties());
		
		DataSeriesImportManager importManager = new DataSeriesImportManagerImpl(bc);
		registerService(bc, importManager, DataSeriesImportManager.class, new Properties());
		
		Properties baseMenuProperties = new Properties();
		baseMenuProperties.setProperty(ServiceProperties.PREFERRED_MENU, "Apps.Data Series");
		baseMenuProperties.setProperty(ServiceProperties.IN_MENU_BAR, "true");
		
		Properties importProperties = new Properties();
		importProperties.putAll(baseMenuProperties);
		importProperties.setProperty(ServiceProperties.TITLE, "Import Data Series");
		ImportDataSeriesTaskFactory importTaskFactory = new ImportDataSeriesTaskFactory(dataSeriesManager, importManager);
		registerService(bc, importTaskFactory, TaskFactory.class, importProperties);

		Properties exportProperties = new Properties();
		exportProperties.putAll(baseMenuProperties);
		exportProperties.setProperty(ServiceProperties.TITLE, "Export Data Series");
		TaskFactory exportTaskFactory = new ExportDataSeriesTaskFactory(dataSeriesManager, storageManager);
		registerService(bc, exportTaskFactory, TaskFactory.class, exportProperties);

		Properties removeDataSeriesProperties = new Properties();
		removeDataSeriesProperties.putAll(baseMenuProperties);
		removeDataSeriesProperties.setProperty(ServiceProperties.TITLE, "Remove Data Series");
		removeDataSeriesProperties.setProperty("insertSeparatorAfter", Boolean.toString(true));
		TaskFactory removeDataSeriesTaskFactory = new RemoveDataSeriesTaskFactory(dataSeriesManager);
		registerService(bc, removeDataSeriesTaskFactory, TaskFactory.class, removeDataSeriesProperties);

		NetworkSelectedParameterPassingTaskFactory<MapColumnTask> mapColumnTaskFactory = new NetworkSelectedParameterPassingTaskFactory<>(MapColumnTask.class, cyApplicationManager, dataSeriesManager, mappingManager, cyApplicationManager);
		Properties mapProperties = new Properties();
		mapProperties.putAll(baseMenuProperties);
		mapProperties.setProperty(ServiceProperties.TITLE, "Map Column to Series");
		registerService(bc, mapColumnTaskFactory, TaskFactory.class, mapProperties);

		TaskFactory removeMappingTaskFactory = new RemoveColumnMappingTaskFactory(mappingManager);
		Properties removeMappingProperties = new Properties();
		removeMappingProperties.putAll(baseMenuProperties);
		removeMappingProperties.setProperty(ServiceProperties.TITLE, "Remove Column Mapping");
		registerService(bc, removeMappingTaskFactory, TaskFactory.class, removeMappingProperties);

		ParameterPassingTaskFactory<ManageMappingsTask> manageMappingTaskFactory = new ParameterPassingTaskFactory<>(ManageMappingsTask.class, serviceRegistrar);
		Properties manageMappingProperties = new Properties();
		manageMappingProperties.putAll(baseMenuProperties);
		manageMappingProperties.setProperty(ServiceProperties.TITLE, "Manage Column Mappings");
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
