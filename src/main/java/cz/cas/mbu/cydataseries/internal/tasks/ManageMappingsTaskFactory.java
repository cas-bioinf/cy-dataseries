package cz.cas.mbu.cydataseries.internal.tasks;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import cz.cas.mbu.cydataseries.DataSeriesManager;


public class ManageMappingsTaskFactory extends AbstractTaskFactory {

	private final CyServiceRegistrar registrar;

	public ManageMappingsTaskFactory(CyServiceRegistrar registrar) {
		super();
		this.registrar = registrar;
	}

	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new ManageMappingsTask(registrar));
	}

	@Override
	public boolean isReady() {
		return !registrar.getService(DataSeriesManager.class).getAllDataSeries().isEmpty();
	}

}
