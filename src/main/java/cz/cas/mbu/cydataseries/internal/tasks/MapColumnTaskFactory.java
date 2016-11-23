package cz.cas.mbu.cydataseries.internal.tasks;

import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import cz.cas.mbu.cydataseries.DataSeriesManager;


public class MapColumnTaskFactory extends AbstractTaskFactory {

	private final CyServiceRegistrar registrar;

	public MapColumnTaskFactory(CyServiceRegistrar registrar) {
		super();
		this.registrar = registrar;
	}

	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new MapColumnTask(registrar));
	}

	@Override
	public boolean isReady() {
		if (registrar.getService(CyNetworkManager.class).getNetworkSet().isEmpty())
		{
			return false;
		}
		return  !registrar.getService(DataSeriesManager.class).getAllDataSeries().isEmpty();
	}

}
