package cz.cas.mbu.cydataseries.internal.tasks;

import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import cz.cas.mbu.cydataseries.DataSeriesManager;
import cz.cas.mbu.cydataseries.SmoothingService;

public class SmoothDataSeriesTaskFactory extends AbstractTaskFactory {

	private final DataSeriesManager dataSeriesManager;
	private final SmoothingService smoothingService;

	public SmoothDataSeriesTaskFactory(DataSeriesManager dataSeriesManager, SmoothingService smoothingService) {
		super();
		this.dataSeriesManager = dataSeriesManager;
		this.smoothingService = smoothingService;
	}

	@Override
	public boolean isReady() {
		return !dataSeriesManager.getAllDataSeries().isEmpty();
	}

	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new SmoothDataSeriesTask(dataSeriesManager, smoothingService));
	}
	
}
