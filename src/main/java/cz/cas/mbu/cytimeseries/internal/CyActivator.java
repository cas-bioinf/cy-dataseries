package cz.cas.mbu.cytimeseries.internal;

import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelComponent2;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.events.RowsSetListener;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.work.ServiceProperties;
import org.cytoscape.work.TaskFactory;
import org.osgi.framework.BundleContext;

import cz.cas.mbu.cytimeseries.DataSeriesManager;

public class CyActivator extends AbstractCyActivator {

	@Override
	public void start(BundleContext bc) throws Exception {
		
		CyNetworkManager cyNetworkManager = getService(bc,CyNetworkManager.class);
		CyApplicationManager cyApplicationManager = getService(bc,CyApplicationManager.class);
		CyTableManager cyTableManager = getService(bc, CyTableManager.class);
		CyTableFactory cyTableFactory = getService(bc, CyTableFactory.class);
		CyNetworkTableManager cyNetworkTableManager = getService(bc, CyNetworkTableManager.class);

		TimeSeriesManagerImpl timeSeriesManager = new TimeSeriesManagerImpl(cyTableManager, cyTableFactory, cyNetworkTableManager);
		registerService(bc, timeSeriesManager, DataSeriesManager.class, new Properties());
		
		NetworkSelectedParameterPassingTaskFactory<AddTimeSeriesTask> addTaskFactory = new NetworkSelectedParameterPassingTaskFactory<>(AddTimeSeriesTask.class, cyApplicationManager, cyApplicationManager, timeSeriesManager);
		
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
		TimeSeriesVisualPanel panel = new TimeSeriesVisualPanel(cyApplicationManager, timeSeriesManager);
		registerService(bc, panel, CytoPanelComponent.class, new Properties());
		registerService(bc, panel, RowsSetListener.class, new Properties());
	}

}
