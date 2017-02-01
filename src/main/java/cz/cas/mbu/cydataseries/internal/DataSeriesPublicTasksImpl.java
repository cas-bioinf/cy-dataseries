package cz.cas.mbu.cydataseries.internal;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

import cz.cas.mbu.cydataseries.DataSeries;
import cz.cas.mbu.cydataseries.DataSeriesPublicTasks;
import cz.cas.mbu.cydataseries.internal.dataimport.ImportDataSeriesTaskFactory;
import cz.cas.mbu.cydataseries.internal.dataimport.ImportSoftFileTaskFactory;

/**
 * Implementation for {@link DataSeriesPublicTasks}, see there for method descriptions.
 * @author MBU
 *
 */
public class DataSeriesPublicTasksImpl implements DataSeriesPublicTasks {

	private final ImportDataSeriesTaskFactory importTabularTaskFactory;
	private final ImportSoftFileTaskFactory importSoftFileTaskFactory;
	private final TaskFactory mapTaskFactory;
	private final TaskFactory interactiveSmoothingTaskFactory;
	
	

	public DataSeriesPublicTasksImpl(ImportDataSeriesTaskFactory importTabularTaskFactory,
			ImportSoftFileTaskFactory importSoftFileTaskFactory, TaskFactory mapTaskFactory,
			TaskFactory interactiveSmoothingTaskFactory) {
		super();
		this.importTabularTaskFactory = importTabularTaskFactory;
		this.importSoftFileTaskFactory = importSoftFileTaskFactory;
		this.mapTaskFactory = mapTaskFactory;
		this.interactiveSmoothingTaskFactory = interactiveSmoothingTaskFactory;
	}



	@Override
	public TaskIterator getImportDataSeriesTabularTask() {
		return importTabularTaskFactory.createTaskIterator();
	}

	
	
	@Override
	public TaskIterator getImportDataSeriesTabularTask(Class<? extends DataSeries<?, ?>> preferredClass) {
		return importTabularTaskFactory.createTaskIterator(preferredClass);
	}

	@Override
	public TaskIterator getImportSoftFileTask() {
		return importSoftFileTaskFactory.createTaskIterator();
	}



	@Override
	public TaskIterator getImportSoftFileTask(Class<? extends DataSeries<?, ?>> preferredClass) {
		return importSoftFileTaskFactory.createTaskIterator(preferredClass);
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
