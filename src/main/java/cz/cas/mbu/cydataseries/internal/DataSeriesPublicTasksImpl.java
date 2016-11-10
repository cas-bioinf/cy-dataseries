package cz.cas.mbu.cydataseries.internal;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

import cz.cas.mbu.cydataseries.DataSeries;
import cz.cas.mbu.cydataseries.DataSeriesPublicTasks;
import cz.cas.mbu.cydataseries.internal.dataimport.ImportDataSeriesTaskFactory;

public class DataSeriesPublicTasksImpl implements DataSeriesPublicTasks {

	private final ImportDataSeriesTaskFactory importTaskFactory;
	private final TaskFactory mapTaskFactory;
	private final TaskFactory interactiveSmoothingTaskFactory;
	
	
	public DataSeriesPublicTasksImpl(ImportDataSeriesTaskFactory importTaskFactory, TaskFactory mapTaskFactory,
			TaskFactory interactiveSmoothingTaskFactory) {
		super();
		this.importTaskFactory = importTaskFactory;
		this.mapTaskFactory = mapTaskFactory;
		this.interactiveSmoothingTaskFactory = interactiveSmoothingTaskFactory;
	}

	@Override
	public TaskIterator getImportDataSeriesTask() {
		return importTaskFactory.createTaskIterator();
	}

	
	
	@Override
	public TaskIterator getImportDataSeriesTask(Class<? extends DataSeries<?, ?>> preferredClass) {
		return importTaskFactory.createTaskIterator(preferredClass);
	}

	@Override
	public TaskIterator getMapDataSeriesTask() {
		return mapTaskFactory.createTaskIterator();
	}

	@Override
	public TaskIterator getInteractiveSmoothingTask() {
		return interactiveSmoothingTaskFactory.createTaskIterator();
	}

	
}
