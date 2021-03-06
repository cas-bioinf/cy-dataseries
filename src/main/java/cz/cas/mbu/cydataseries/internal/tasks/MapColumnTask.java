package cz.cas.mbu.cydataseries.internal.tasks;

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
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.util.ListSingleSelection;

import cz.cas.mbu.cydataseries.DataSeries;
import cz.cas.mbu.cydataseries.DataSeriesException;
import cz.cas.mbu.cydataseries.DataSeriesManager;
import cz.cas.mbu.cydataseries.DataSeriesMappingManager;
import cz.cas.mbu.cydataseries.internal.AlphanumComparator;

public class MapColumnTask extends AbstractValidatedTask {
	
	private final Logger userLogger = Logger.getLogger(CyUserLog.NAME); 

	@Tunable(description = "Data series:")
	public ListSingleSelection<DataSeries<?,?>> dataSeries;

	@Tunable(description = "Network to attach:")
	public ListSingleSelection<CyNetwork> targetNetwork;
	
	
	@Tunable(description = "Attach the series to:")
	public ListSingleSelection<TargetClassInfo> targetClass;
	
	@Tunable(description = "Create new column for the mapping", groups={"Column"})
	public boolean createNewColumn = true;
	
	@Tunable(description = "New column name", groups={"Column"}, dependsOn="createNewColumn=true")
	public String newColumnName;

	private ListSingleSelection<String> existingColumnForMapping;
	
	private boolean updatingExistingColumnForMapping = false;
	
	@Tunable(description="Existing column", groups={"Column"}, dependsOn="createNewColumn=false", listenForChange ={"targetClass","targetNetwork"})
	public ListSingleSelection<String> getExistingColumnForMapping()
	{
		if(!updatingExistingColumnForMapping)
		{
			updatingExistingColumnForMapping = true;
			try {
				updateExistingColumnForMapping(targetClass.getSelectedValue().getTargetClass());
			} finally {
				updatingExistingColumnForMapping = false;
			}
		}
		return existingColumnForMapping;
	}
	
	public void setExistingColumnForMapping(ListSingleSelection<String> columnsForMapping)
	{
		this.existingColumnForMapping = columnsForMapping;
	}
	
	@Tunable(description = "Map by matching row names to existing column", groups={"Mapping"})
	public boolean mapByRowNames = true;

	private ListSingleSelection<String> mapRowNamesWithColumn;
	
	private boolean updatingMapRowNamesWithColumn = false;
	
	@Tunable(description="Column to match row names", groups={"Mapping"}, dependsOn="mapByRowNames=true", listenForChange ="targetClass")
	public ListSingleSelection<String> getMapRowNamesWithColumn()
	{
		if(!updatingMapRowNamesWithColumn)
		{
			updatingMapRowNamesWithColumn = true;
			try 
			{
				updateMapRowNamesWithColumn(targetClass.getSelectedValue().getTargetClass());
			} finally {
				updatingMapRowNamesWithColumn = false;
			}
		}
		return mapRowNamesWithColumn;
	}
	
	public void setMapRowNamesWithColumn(ListSingleSelection<String> mapRowNamesWithColumn)
	{
		this.mapRowNamesWithColumn = mapRowNamesWithColumn;
	}
	
	private final DataSeriesMappingManager mappingManager;

	private final CyApplicationManager applicationManager;
	
	public MapColumnTask(CyServiceRegistrar registrar) {
		this.applicationManager = registrar.getService(CyApplicationManager.class);
		this.mappingManager = registrar.getService(DataSeriesMappingManager.class);
		
		DataSeriesManager dataSeriesManager = registrar.getService(DataSeriesManager.class);
		CyNetworkManager networkManager = registrar.getService(CyNetworkManager.class);
		
		targetClass = new ListSingleSelection<>(new TargetClassInfo("Nodes", CyNode.class), new TargetClassInfo("Edges", CyEdge.class));
		dataSeries = new ListSingleSelection<>(dataSeriesManager.getAllDataSeries());
		existingColumnForMapping = new ListSingleSelection<>();
		mapRowNamesWithColumn = new ListSingleSelection<>();
		
		targetNetwork = new ListSingleSelection<>(networkManager.getNetworkSet().stream().toArray(CyNetwork[]::new));
		if(applicationManager.getCurrentNetwork() != null)
		{
			targetNetwork.setSelectedValue(applicationManager.getCurrentNetwork());
		}
		
		updateExistingColumnForMapping(CyNode.class);
		updateMapRowNamesWithColumn(CyNode.class);
		
	}
	
	
	@ProvidesTitle
	public String getTitle()
	{
		return "Map data series to network";
	}
	
	private void updateExistingColumnForMapping(Class<? extends CyIdentifiable> targetClass)
	{
		showColumnsForClass(DataSeriesMappingManager.MAPPING_COLUMN_CLASS, existingColumnForMapping, targetClass);		
	}

	private void updateMapRowNamesWithColumn(Class<? extends CyIdentifiable> targetClass)
	{
		showColumnsForClass(String.class, mapRowNamesWithColumn, targetClass);
		if(mapRowNamesWithColumn.getPossibleValues().contains("name") && mapRowNamesWithColumn.getSelectedValue() == null)
		{
			mapRowNamesWithColumn.setSelectedValue("name");
		}
	}
	
	private void showColumnsForClass(Class<?> columnType, ListSingleSelection<String> selection, Class<? extends CyIdentifiable> targetClass)
	{
		CyNetwork network = targetNetwork.getSelectedValue();
		List<CyColumn> candidateColumns = new ArrayList<>(mappingManager.getMappingTable(network, targetClass).getColumns());
		List<String> filteredCandidateColumnsNames = candidateColumns.stream()
				.filter(col -> col.getType() == columnType && !col.isPrimaryKey())
				.map(col -> col.getName())
				.collect(Collectors.toList());
		
		filteredCandidateColumnsNames.sort(new AlphanumComparator<>());
		
		selection.setPossibleValues(filteredCandidateColumnsNames);
		
	}
	
	@Override
	public void run(TaskMonitor tm) throws Exception {
		CyNetwork network = targetNetwork.getSelectedValue();
		CyTable targetTable = mappingManager.getMappingTable(network, targetClass.getSelectedValue().getTargetClass());
		
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
		
		mappingManager.mapDataSeriesRowsToTableColumn(targetNetwork.getSelectedValue(), targetClass.getSelectedValue().targetClass, mappingColumn.getName(), dataSeries.getSelectedValue());
		
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
					List<String> rowNames = dataSeries.getSelectedValue().getRowNames();
					int rowIndex = rowNames.indexOf(rowNameInData);
					if(rowIndex < 0)
					{
						row.set(mappingColumn.getName(), null);					
						notMapped++;
					}
					else
					{
						int rowID = dataSeries.getSelectedValue().getRowID(rowIndex);
						if (rowNames.lastIndexOf(rowNameInData) != rowIndex)
						{
							userLogger.warn("The data series '" + dataSeries.getSelectedValue().getName() + "' contains multiple rows with name '" +  rowNameInData + "'. Mapping the node '" + rowNameInData + "' to row ID " + rowID); 
						}
						row.set(mappingColumn.getName(), rowID);						
						mapped++;
					}
				}				
			}
			
			userLogger.info("Mapped " + mapped + " rows to data series " + dataSeries.getSelectedValue().getName() + ", " + notMapped + " rows could not be mapped, " + empty + " rows were empty.");			
		}
	}	
	
	
	
	@Override
	public ValidationState getValidationState(StringBuilder errMsg) {
		ValidationState result = ValidationState.OK;
		CyNetwork network = targetNetwork.getSelectedValue();
		CyTable targetTable = mappingManager.getMappingTable(network, targetClass.getSelectedValue().getTargetClass());
		
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
		
		if(mapByRowNames && mapRowNamesWithColumn.getSelectedValue() == null)
		{
			errMsg.append("You have to select a column to match row names");
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
			
		DataSeries<?, ?> currentMappingTarget = mappingManager.getMappedDataSeries(targetNetwork.getSelectedValue(), targetClass.getSelectedValue().getTargetClass(), targetColumnName); 
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
