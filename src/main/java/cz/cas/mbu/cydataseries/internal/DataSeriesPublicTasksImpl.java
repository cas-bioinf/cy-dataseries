package cz.cas.mbu.cydataseries.internal;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

import cz.cas.mbu.cydataseries.DataSeriesPublicTasks;

public class DataSeriesPublicTasksImpl implements DataSeriesPublicTasks {

	private final TaskFactory importTaskFactory;
	private final TaskFactory mapTaskFactory;
	private final TaskFactory interactiveSmoothingTaskFactory;
	
	
	public DataSeriesPublicTasksImpl(TaskFactory importTaskFactory, TaskFactory mapTaskFactory,
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
	public TaskIterator getMapDataSeriesTask() {
		return mapTaskFactory.createTaskIterator();
	}

	@Override
	public TaskIterator getInteractiveSmoothingTask() {
		return interactiveSmoothingTaskFactory.createTaskIterator();
	}

	
}
