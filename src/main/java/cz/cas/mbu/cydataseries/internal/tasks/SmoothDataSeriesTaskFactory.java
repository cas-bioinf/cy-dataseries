package cz.cas.mbu.cydataseries.internal.tasks;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import cz.cas.mbu.cydataseries.DataSeriesManager;
import cz.cas.mbu.cydataseries.SmoothingService;

public class SmoothDataSeriesTaskFactory extends AbstractTaskFactory {

	private final CyServiceRegistrar registrar;
	private final DataSeriesManager dataSeriesManager;


	public SmoothDataSeriesTaskFactory(CyServiceRegistrar registrar) {
		super();
		this.registrar = registrar;
		dataSeriesManager = registrar.getService(DataSeriesManager.class);
	}

	@Override
	public boolean isReady() {
		return !dataSeriesManager.getAllDataSeries().isEmpty();
	}

	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new SmoothDataSeriesTask(registrar));
	}
	
}
