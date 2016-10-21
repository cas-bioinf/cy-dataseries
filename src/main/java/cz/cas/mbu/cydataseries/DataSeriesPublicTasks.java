package cz.cas.mbu.cydataseries;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public interface DataSeriesPublicTasks {
	TaskIterator getImportDataSeriesTask();
	TaskIterator getMapDataSeriesTask();
	TaskIterator getInteractiveSmoothingTask();
}
