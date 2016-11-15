package cz.cas.mbu.cydataseries.internal.tasks;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import cz.cas.mbu.cydataseries.DataSeriesManager;
import cz.cas.mbu.cydataseries.DataSeriesMappingManager;


public class MapColumnTaskFactory extends AbstractTaskFactory {

	private final CyServiceRegistrar registrar;

	public MapColumnTaskFactory(CyServiceRegistrar registrar) {
		super();
		this.registrar = registrar;
	}

	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new MapColumnTask(registrar.getService(DataSeriesManager.class), registrar.getService(DataSeriesMappingManager.class), registrar.getService(CyApplicationManager.class)));
	}

	@Override
	public boolean isReady() {
		return !registrar.getService(DataSeriesManager.class).getAllDataSeries().isEmpty();
	}

}
