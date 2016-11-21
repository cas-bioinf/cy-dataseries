package cz.cas.mbu.cydataseries.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyIdentifiable;
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

	private void setColumnIndicesForNewSeriesMapping(Class<? extends CyIdentifiable> targetClass, DataSeries<?,?> sourceSeries, String sourceColumn, DataSeries<?,?> smoothedSeries, String targetColumn, Map<Integer, Integer> rowMapping)
	{
		CyTable defaultTable = Utils.getDefaultTable(registrar, targetClass);
		if(rowMapping != null)
		{
			defaultTable.getAllRows().forEach(
					row -> 
						row.set(targetColumn, rowMapping.get(row.get(sourceColumn, DataSeriesMappingManager.MAPPING_COLUMN_CLASS)))
				);
		}
		else
		{
			defaultTable.getAllRows().forEach(
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
			mappingManager.unmapTableColumn(descriptor.getTargetClass(), descriptor.getColumnName());

			//No need to write anything if rows were not grouped
			if(rowGrouping != null)
			{
				setColumnIndicesForNewSeriesMapping(descriptor.getTargetClass(), sourceTimeSeries, descriptor.getColumnName(), targetTimeSeries, descriptor.getColumnName(), rowMapping);
			}
			
			mappingManager.mapDataSeriesRowsToTableColumn(descriptor.getTargetClass(), descriptor.getColumnName(), targetTimeSeries);
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
					CyTable defaultTable = Utils.getDefaultTable(registrar, descriptor.getTargetClass());
					if (defaultTable.getColumn(newColumnName) == null)
					{
						defaultTable.createColumn(newColumnName, DataSeriesMappingManager.MAPPING_COLUMN_CLASS, false);
					}
					setColumnIndicesForNewSeriesMapping(descriptor.getTargetClass(), sourceTimeSeries, descriptor.getColumnName(), targetTimeSeries, newColumnName, rowMapping);
					mappingManager.mapDataSeriesRowsToTableColumn(descriptor.getTargetClass(), newColumnName, targetTimeSeries);
				});				
	}

}
