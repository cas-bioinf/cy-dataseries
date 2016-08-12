package cz.cas.mbu.cydataseries.internal;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

import cz.cas.mbu.cydataseries.DataSeriesPublicTasks;

public class DataSeriesPublicTasksImpl implements DataSeriesPublicTasks {

	private final TaskFactory importTaskFactory;
	private final TaskFactory mapTaskFactory;
	
	
	public DataSeriesPublicTasksImpl(TaskFactory importTaskFactory, TaskFactory mapTaskFactory) {
		super();
		this.importTaskFactory = importTaskFactory;
		this.mapTaskFactory = mapTaskFactory;
	}

	@Override
	public TaskIterator getImportDataSeriesTask() {
		return importTaskFactory.createTaskIterator();
	}

	@Override
	public TaskIterator getMapDataSeriesTask() {
		return mapTaskFactory.createTaskIterator();
	}

}
