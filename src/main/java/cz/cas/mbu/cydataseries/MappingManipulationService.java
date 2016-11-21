package cz.cas.mbu.cydataseries;

import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyTable;

import cz.cas.mbu.cydataseries.internal.Utils;

public interface MappingManipulationService {

	void copyMapping(DataSeries<?,?> sourceTimeSeries, DataSeries<?,?> targetTimeSeries, Map<String, List<Integer>> rowGrouping, String mappingSuffix);

	void replaceMapping(DataSeries<?,?> sourceTimeSeries, DataSeries<?,?> targetTimeSeries, Map<String, List<Integer>> rowGrouping);
}
