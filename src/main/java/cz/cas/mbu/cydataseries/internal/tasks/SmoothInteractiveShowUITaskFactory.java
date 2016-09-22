package cz.cas.mbu.cydataseries.internal.tasks;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import cz.cas.mbu.cydataseries.DataSeriesManager;

public class SmoothInteractiveShowUITaskFactory extends AbstractTaskFactory {

	private final CyServiceRegistrar registrar;

	public SmoothInteractiveShowUITaskFactory(CyServiceRegistrar registrar) {
		super();
		this.registrar = registrar;
	}

	@Override
	public boolean isReady() {
		return !registrar.getService(DataSeriesManager.class).getAllDataSeries().isEmpty();
	}

	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new SmoothInteractiveShowUITask(registrar));
	}
	
}
