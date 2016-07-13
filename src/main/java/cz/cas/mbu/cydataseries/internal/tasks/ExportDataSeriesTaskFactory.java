package cz.cas.mbu.cydataseries.internal.tasks;

import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import cz.cas.mbu.cydataseries.DataSeriesManager;
import cz.cas.mbu.cydataseries.DataSeriesStorageManager;


public class ExportDataSeriesTaskFactory extends AbstractTaskFactory {

	private final DataSeriesManager dataSeriesManager;
	private final DataSeriesStorageManager dataSeriesStorageManager;

	public ExportDataSeriesTaskFactory(DataSeriesManager dataSeriesManager,
			DataSeriesStorageManager dataSeriesStorageManager) {
		super();
		this.dataSeriesManager = dataSeriesManager;
		this.dataSeriesStorageManager = dataSeriesStorageManager;
	}

	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new ExportDataSeriesTask(dataSeriesManager, dataSeriesStorageManager));
	}

	@Override
	public boolean isReady() {
		return !dataSeriesManager.getAllDataSeries().isEmpty();
	}

}
