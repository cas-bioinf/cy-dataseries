package cz.cas.mbu.cydataseries.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.service.util.CyServiceRegistrar;

import cz.cas.mbu.cydataseries.DataSeries;
import cz.cas.mbu.cydataseries.DataSeriesMappingManager;
import cz.cas.mbu.cydataseries.MappingDescriptor;
import cz.cas.mbu.cydataseries.MappingManipulationService;

public class MappingManipulationServiceImpl implements MappingManipulationService {
	private final CyServiceRegistrar registrar;	
	
	public MappingManipulationServiceImpl(CyServiceRegistrar registrar){
		super();
		this.registrar = registrar;
	}

	private void setColumnIndicesForNewSeriesMapping(CyTable table, DataSeries<?,?> sourceSeries, String sourceColumn, DataSeries<?,?> smoothedSeries, String targetColumn, Map<Integer, Integer> rowMapping)
	{
		if(rowMapping != null)
		{
			table.getAllRows().forEach(
					row -> 
						row.set(targetColumn, rowMapping.get(row.get(sourceColumn, DataSeriesMappingManager.MAPPING_COLUMN_CLASS)))
				);
		}
		else
		{
			table.getAllRows().forEach(
					row -> 
						row.set(targetColumn, row.get(sourceColumn, DataSeriesMappingManager.MAPPING_COLUMN_CLASS))
				);			
		}
	}
	
	private Map<Integer, Integer> rowMappingFromRowGrouping(Map<String, List<Integer>> rowGrouping,
			DataSeries<?, ?> targetTimeSeries) {
		if(rowGrouping == null)
		{
			return null;
		}
		
		Map<Integer, Integer> rowMapping = new HashMap<>();
		rowGrouping.forEach((smoothedRowName, originalRowIds) ->
		{
			int smoothedRowId = targetTimeSeries.getRowID(targetTimeSeries.getRowNames().indexOf(smoothedRowName));
			originalRowIds.forEach(originalId -> rowMapping.put(originalId, smoothedRowId));
		});
		return rowMapping;
	}
		
	@Override
	public void replaceMapping(DataSeries<?,?> sourceTimeSeries, DataSeries<?,?> targetTimeSeries, Map<String, List<Integer>> rowGrouping) {
		final DataSeriesMappingManager mappingManager = registrar.getService(DataSeriesMappingManager.class);
		List<MappingDescriptor<DataSeries<?,?>>> allDescriptors = mappingManager.getMappingDescriptorsForSeries(sourceTimeSeries);
		
		Map<Integer, Integer> rowMapping = rowMappingFromRowGrouping(rowGrouping, targetTimeSeries);
		
		allDescriptors.forEach(descriptor -> {
			mappingManager.unmap(descriptor);

			//No need to write anything if rows were not grouped
			if(rowGrouping != null)
			{
				CyTable table = mappingManager.getMappingTable(descriptor.getNetwork(), descriptor.getTargetClass());
				setColumnIndicesForNewSeriesMapping(table, sourceTimeSeries, descriptor.getColumnName(), targetTimeSeries, descriptor.getColumnName(), rowMapping);
			}
			
			mappingManager.mapDataSeriesRowsToTableColumn(descriptor.getNetwork(), descriptor.getTargetClass(), descriptor.getColumnName(), targetTimeSeries);
		});
	}

    @Override
	public void copyMapping(DataSeries<?,?> sourceTimeSeries, DataSeries<?,?> targetTimeSeries, Map<String, List<Integer>> rowGrouping, String mappingSuffix)
    {
		final DataSeriesMappingManager mappingManager = registrar.getService(DataSeriesMappingManager.class);
		List<MappingDescriptor<DataSeries<?,?>>> allDescriptors = mappingManager.getMappingDescriptorsForSeries(sourceTimeSeries);
		
		Map<Integer, Integer> rowMapping = rowMappingFromRowGrouping(rowGrouping, targetTimeSeries);
				allDescriptors.forEach(descriptor -> {
					String newColumnName = descriptor.getColumnName() + mappingSuffix;
					CyTable table = mappingManager.getMappingTable(descriptor.getNetwork(), descriptor.getTargetClass());
					if (table.getColumn(newColumnName) == null)
					{
						table.createColumn(newColumnName, DataSeriesMappingManager.MAPPING_COLUMN_CLASS, false);
					}
					setColumnIndicesForNewSeriesMapping(table, sourceTimeSeries, descriptor.getColumnName(), targetTimeSeries, newColumnName, rowMapping);
					mappingManager.mapDataSeriesRowsToTableColumn(descriptor.getNetwork(), descriptor.getTargetClass(), newColumnName, targetTimeSeries);
				});				
	}

}
