package cz.cas.mbu.cydataseries;

import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyTable;

import cz.cas.mbu.cydataseries.internal.Utils;

/**
 * Utility functions to work with mappings.
 * @author Martin
 *
 */
public interface MappingManipulationService {

	/**
	 * For each mapping of source series create a new column and a new mapping of the target series.
	 * @param sourceTimeSeries
	 * @param targetTimeSeries
	 * @param rowGrouping Determines how the mapping should be transformed. keys: row names of the target series, values: ids of rows in the source series
	 * @param mappingSuffix suffix given to the newly created columns that map to the target series
	 */
	void copyMapping(DataSeries<?,?> sourceTimeSeries, DataSeries<?,?> targetTimeSeries, Map<String, List<Integer>> rowGrouping, String mappingSuffix);

	/**
	 * Remove all mappings of the source time series and replace them with mappings of the target series to the same columns.  
	 * @param sourceTimeSeries
	 * @param targetTimeSeries
	 * @param rowGrouping Determines how the mapping should be transformed. keys: row names of the target series, values: ids of rows in the source series
	 */
	void replaceMapping(DataSeries<?,?> sourceTimeSeries, DataSeries<?,?> targetTimeSeries, Map<String, List<Integer>> rowGrouping);
}
