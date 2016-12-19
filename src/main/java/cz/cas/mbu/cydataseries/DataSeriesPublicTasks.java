package cz.cas.mbu.cydataseries;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

/**
 * Tasks that other plugins may want to invoke.
 * @author Martin
 *
 */
public interface DataSeriesPublicTasks {
	TaskIterator getImportDataSeriesTabularTask();
	TaskIterator getImportDataSeriesTabularTask(Class<? extends DataSeries<?, ?>> preferredClass);
	TaskIterator getImportSoftFileTask();
	TaskIterator getImportSoftFileTask(Class<? extends DataSeries<?, ?>> preferredClass);
	TaskIterator getMapDataSeriesTask();
	TaskIterator getInteractiveSmoothingTask();
}
