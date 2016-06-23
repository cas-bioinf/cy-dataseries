package cz.cas.mbu.cytimeseries.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.util.ListSingleSelection;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

import cz.cas.mbu.cytimeseries.DataSeries;
import cz.cas.mbu.cytimeseries.DataSeriesException;
import cz.cas.mbu.cytimeseries.DataSeriesManager;
import cz.cas.mbu.cytimeseries.DataSeriesMappingManager;

public class MapColumnTask extends AbstractTask implements TunableValidator{
	
	private final Logger userLogger = Logger.getLogger(CyUserLog.NAME); 

	@Tunable(description = "Data series:")
	public ListSingleSelection<DataSeries<?,?>> dataSeries;
	
	@Tunable(description = "Attach the series to:")
	public ListSingleSelection<TargetClassInfo> targetClass;
	
	@Tunable(description = "Create new column for the mapping", groups={"Column"})
	public boolean createNewColumn = true;
	
	@Tunable(description = "New column name", groups={"Column"}, dependsOn="createNewColumn=true")
	public String newColumnName;

	private ListSingleSelection<String> existingColumnForMapping;
	
	@Tunable(description="Existing column", groups={"Column"}, dependsOn="createNewColumn=false", listenForChange ="targetClass")
	public ListSingleSelection<String> getExistingColumnForMapping()
	{
		updateExistingColumnForMapping(targetClass.getSelectedValue().getTargetClass());
		return existingColumnForMapping;
	}
	
	public void setExistingColumnForMapping(ListSingleSelection<String> columnsForMapping)
	{
		this.existingColumnForMapping = columnsForMapping;
	}
	
	@Tunable(description = "Map by matching row names to existing column", groups={"Mapping"})
	public boolean mapByRowNames = false;

	private ListSingleSelection<String> mapRowNamesWithColumn;
	
	@Tunable(description="Column to match row names", groups={"Mapping"}, dependsOn="mapByRowNames=true", listenForChange ="targetClass")
	public ListSingleSelection<String> getMapRowNamesWithColumn()
	{
		updateMapRowNamesWithColumn(targetClass.getSelectedValue().getTargetClass());
		return mapRowNamesWithColumn;
	}
	
	public void setMapRowNamesWithColumn(ListSingleSelection<String> mapRowNamesWithColumn)
	{
		this.mapRowNamesWithColumn = mapRowNamesWithColumn;
	}
	
	private final DataSeriesMappingManager mappingManager;

	private final CyApplicationManager applicationManager;
	
	public MapColumnTask(DataSeriesManager dataSeriesManager, DataSeriesMappingManager mappingManager, CyApplicationManager applicationManager) {
		this.applicationManager = applicationManager;
		this.mappingManager = mappingManager;
		targetClass = new ListSingleSelection<>(new TargetClassInfo("Nodes", CyNode.class), new TargetClassInfo("Edges", CyEdge.class));
		dataSeries = new ListSingleSelection<>(dataSeriesManager.getAllDataSeries());
		existingColumnForMapping = new ListSingleSelection<>();
		updateExistingColumnForMapping(CyNode.class);
		mapRowNamesWithColumn = new ListSingleSelection<>();
		updateMapRowNamesWithColumn(CyNode.class);
	}
	
	
	private void updateExistingColumnForMapping(Class<? extends CyIdentifiable> targetClass)
	{
		showColumnsForClass(DataSeriesMappingManager.MAPPING_COLUMN_CLASS, existingColumnForMapping, targetClass);		
	}

	private void updateMapRowNamesWithColumn(Class<? extends CyIdentifiable> targetClass)
	{
		showColumnsForClass(String.class, mapRowNamesWithColumn, targetClass);		
	}
	
	private void showColumnsForClass(Class<?> columnType, ListSingleSelection<String> selection, Class<? extends CyIdentifiable> targetClass)
	{
		CyNetwork network = applicationManager.getCurrentNetwork();
		List<CyColumn> candidateColumns = new ArrayList<>(network.getTable(targetClass, CyNetwork.DEFAULT_ATTRS).getColumns());
		List<String> filteredCandidateColumnsNames = candidateColumns.stream()
				.filter(col -> col.getType() == columnType && !col.isPrimaryKey())
				.map(col -> col.getName())
				.collect(Collectors.toList());
		
		filteredCandidateColumnsNames.sort(new AlphanumComparator<>());
		
		selection.setPossibleValues(filteredCandidateColumnsNames);
		
	}
	
	@Override
	public void run(TaskMonitor tm) throws Exception {
		CyNetwork network = applicationManager.getCurrentNetwork();
		CyTable targetTable = network.getTable(targetClass.getSelectedValue().getTargetClass(), CyNetwork.DEFAULT_ATTRS);
		
		//Create the actual mapping
		CyColumn mappingColumn = null;
		if(createNewColumn)
		{
			targetTable.createColumn(newColumnName, DataSeriesMappingManager.MAPPING_COLUMN_CLASS, false);
			mappingColumn = targetTable.getColumn(newColumnName);
		}
		else
		{
			mappingColumn = targetTable.getColumn(existingColumnForMapping.getSelectedValue());
		}
		
		if(mappingColumn == null)
		{
			throw new DataSeriesException("Could not get/create the column for mapping");
		}
		
		if(!mappingColumn.getType().equals(DataSeriesMappingManager.MAPPING_COLUMN_CLASS))
		{
			throw new DataSeriesException("The mapping column is of wrong type (should be " + DataSeriesMappingManager.MAPPING_COLUMN_CLASS.getSimpleName() + ", is" + mappingColumn.getType().getSimpleName() + ")");
		}
		
		mappingManager.mapDataSeriesRowsToTableColumn(targetClass.getSelectedValue().targetClass, mappingColumn.getName(), dataSeries.getSelectedValue());
		
		//Fill the mapping column 
		if(mapByRowNames)
		{
			CyColumn rowNamesColumn = targetTable.getColumn(mapRowNamesWithColumn.getSelectedValue());
			if(rowNamesColumn == null || !rowNamesColumn.getType().equals(String.class))
			{
				throw new DataSeriesException("The column for row names matches (" + mapRowNamesWithColumn.getSelectedValue() + ") has to exist and be of type string.");
			}
			
			int mapped = 0;
			int notMapped = 0;
			int empty = 0;
			for(CyRow row : targetTable.getAllRows())
			{
				String rowNameInData = row.get(mapRowNamesWithColumn.getSelectedValue(), String.class);
				if(rowNameInData == null || rowNameInData.isEmpty())
				{
					row.set(mappingColumn.getName(), null);					
					empty++;
				}
				else
				{
					int rowIndex = dataSeries.getSelectedValue().getRowNames().indexOf(rowNameInData);
					if(rowIndex < 0)
					{
						row.set(mappingColumn.getName(), null);					
						notMapped++;
					}
					else
					{
						int rowID = dataSeries.getSelectedValue().getRowID(rowIndex);
						row.set(mappingColumn.getName(), rowID);						
						mapped++;
					}
				}				
			}
			
			userLogger.info("Mapped " + mapped + " rows to data series " + dataSeries.getSelectedValue().getName() + ", " + notMapped + " rows could not be mapped, " + empty + " rows were empty.");			
		}
	}	
	
	
	
	@Override
	public ValidationState getValidationState(Appendable errMsg) {
		ValidationState result = ValidationState.OK;
		try {
			CyNetwork network = applicationManager.getCurrentNetwork();
			CyTable targetTable = network.getTable(targetClass.getSelectedValue().getTargetClass(), CyNetwork.DEFAULT_ATTRS);
			
			if(createNewColumn && targetTable.getColumn(newColumnName) != null)
			{
				errMsg.append("Column with name '" + newColumnName + "' already exists.");
				return ValidationState.INVALID;
			}
			
			if(createNewColumn && (newColumnName == null || newColumnName.isEmpty()))
			{
				errMsg.append("You have to specify a new column name.");
				return ValidationState.INVALID;
			}
			
			String targetColumnName;
			if(createNewColumn)
			{
				targetColumnName = newColumnName;
			}
			else
			{
				targetColumnName = existingColumnForMapping.getSelectedValue();
			}
			
			DataSeries<?, ?> currentMappingTarget = mappingManager.getMappedDataSeries(targetClass.getSelectedValue().getTargetClass(), targetColumnName); 
			if(currentMappingTarget != null)
			{
				errMsg.append("The column '" + targetColumnName + "' is already mapped to data series '" + currentMappingTarget.getName() + "' do you want to overwrite the mapping?\n");
				result = ValidationState.REQUEST_CONFIRMATION;
			}
			
			if(!createNewColumn && mapByRowNames)
			{
				errMsg.append("This will overwrite the contents of column '" + existingColumnForMapping.getSelectedValue() + "', are you sure?\n");
				result = ValidationState.REQUEST_CONFIRMATION;
			}
			
		}
		catch (IOException ex)
		{
			return ValidationState.INVALID;
		}
		return result;
	}



	private class TargetClassInfo
	{
		private String displayName;
		private Class<? extends CyIdentifiable> targetClass;
		
		public TargetClassInfo(String displayName, Class<? extends CyIdentifiable> targetClass) {
			super();
			this.displayName = displayName;
			this.targetClass = targetClass;
		}

		@Override
		public String toString() {
			return displayName;
		}

		public Class<? extends CyIdentifiable> getTargetClass() {
			return targetClass;
		}
		
		
	}	
}
