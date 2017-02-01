package cz.cas.mbu.cydataseries;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

import cz.cas.mbu.cydataseries.dataimport.DataSeriesImportProvider;

/**
 * Tasks that other plugins may want to invoke.
 * @author Martin
 *
 */
public interface DataSeriesPublicTasks {
	/**
	 * Task to import DS from a .TSV/.CSV or similar file
	 * @return
	 */
	TaskIterator getImportDataSeriesTabularTask();
	/**
	 * Task to import DS from a .TSV/.CSV or similar file
	 * @param preferredClass This DS type will be preselected for import (if a corresponding {@link DataSeriesImportProvider} service is available). 
	 * @return
	 */
	TaskIterator getImportDataSeriesTabularTask(Class<? extends DataSeries<?, ?>> preferredClass);
	
	/**
	 * Task to import DS from a SOFT file (as used by Gene expression omnibus)
	 * @return
	 */
	TaskIterator getImportSoftFileTask();

	/**
	 * Task to import DS from a SOFT file (as used by Gene expression omnibus)
	 * @param preferredClass This DS type will be preselected for import (if a corresponding {@link DataSeriesImportProvider} service is available). 
	 * @return
	 */
	TaskIterator getImportSoftFileTask(Class<? extends DataSeries<?, ?>> preferredClass);
	
	/**
	 * Task to add a mapping of a DS to network entity.
	 * @return
	 */
	TaskIterator getMapDataSeriesTask();
	
	/**
	 * Task to invoke the interactive smoothing tool.
	 * @return
	 */
	TaskIterator getInteractiveSmoothingTask();
}
