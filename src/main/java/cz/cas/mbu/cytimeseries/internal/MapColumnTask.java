package cz.cas.mbu.cytimeseries.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import cz.cas.mbu.cytimeseries.DataSeries;
import cz.cas.mbu.cytimeseries.DataSeriesManager;
import cz.cas.mbu.cytimeseries.DataSeriesMappingManager;

public class MapColumnTask extends AbstractTask {
	/*
	@Tunable
	public CyNetwork network;	
*/
	@Tunable(description = "Data series:")
	public ListSingleSelection<DataSeries<?,?>> dataSeries;
	
	@Tunable(description = "Attach the series to:")
	public ListSingleSelection<TargetClassInfo> targetClass;
	
	@Tunable(description = "Create new column for the mapping", groups={"Column"})
	public boolean createNewColumn;
	
	@Tunable(description = "New column name", groups={"Column"}, dependsOn="createNewColumn=true")
	public String newColumnName;

	private ListSingleSelection<String> columnsForMapping;
	
	@Tunable(description="Existing column", groups={"Column"}, dependsOn="createNewColumn=false")
	public ListSingleSelection<String> getColumnsForMapping()
	{
		showColumnsForClass(targetClass.getSelectedValue().getTargetClass());
		return columnsForMapping;
	}
	
	public void setColumnsForMapping(ListSingleSelection<String> columnsForMapping)
	{
		this.columnsForMapping = columnsForMapping;
	}
	

	
	private final DataSeriesMappingManager mappingManager;

	private final CyApplicationManager applicationManager;
	
	public MapColumnTask(DataSeriesManager dataSeriesManager, DataSeriesMappingManager mappingManager, CyApplicationManager applicationManager) {
		this.applicationManager = applicationManager;
		this.mappingManager = mappingManager;
		targetClass = new ListSingleSelection<>(new TargetClassInfo("Nodes", CyNode.class), new TargetClassInfo("Edges", CyEdge.class));
		dataSeries = new ListSingleSelection<>(dataSeriesManager.getAllDataSeries());
		columnsForMapping = new ListSingleSelection<>();
		showColumnsForClass(CyNode.class);
	}
	
	
	
	public void showColumnsForClass(Class<? extends CyIdentifiable> targetClass)
	{
		CyNetwork network = applicationManager.getCurrentNetwork();
		List<CyColumn> candidateColumns = new ArrayList<>(network.getTable(targetClass, CyNetwork.DEFAULT_ATTRS).getColumns());
		List<String> filteredCandidateColumnsNames = candidateColumns.stream()
				.filter(col -> col.getType() == DataSeriesMappingManager.MAPPING_COLUMN_CLASS && !col.isPrimaryKey())
				.map(col -> col.getName())
				.collect(Collectors.toList());
		
		filteredCandidateColumnsNames.sort(new AlphanumComparator<>());
		
		columnsForMapping.setPossibleValues(filteredCandidateColumnsNames);
		
	}
	
	@Override
	public void run(TaskMonitor tm) throws Exception {
		mappingManager.mapDataSeriesRowsToTableColumn(targetClass.getSelectedValue().targetClass, newColumnName, dataSeries.getSelectedValue());
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
