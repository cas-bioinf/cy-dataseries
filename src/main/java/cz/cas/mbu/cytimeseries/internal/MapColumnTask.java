package cz.cas.mbu.cytimeseries.internal;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
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
	@Tunable(description = "Attach the time series to:")
	public ListSingleSelection<TargetClassInfo> targetClass;
	
	@Tunable(description = "New column name:")
	public String newColumnName;

	@Tunable(description = "Data series:")
	public ListSingleSelection<DataSeries<?,?>> dataSeries;

	
	private final DataSeriesMappingManager mappingManager;

	public MapColumnTask(DataSeriesManager dataSeriesManager, DataSeriesMappingManager mappingManager) {
		this.mappingManager = mappingManager;
		targetClass = new ListSingleSelection<>(new TargetClassInfo("Nodes", CyNode.class), new TargetClassInfo("Edges", CyEdge.class));
		dataSeries = new ListSingleSelection<>(dataSeriesManager.getAllDataSeries());
	}
	/*
	@Tunable(description="Columns")
	public ListSingleSelection<String> targetColumn;
	
	
	public void ShowColumnsForClass(Class<? extends CyIdentifiable> targetClass)
	{
		List<CyColumn> candidateColumns = new ArrayList<>(network.getTable(targetClass, CyNetwork.DEFAULT_ATTRS).getColumns());
		List<String> filteredCandidateColumnsNames = new ArrayList<>(candidateColumns.size());
		for(CyColumn col : candidateColumns)
		{
			//Consider only double columns
			if(col.getType() == Double.class && !col.isPrimaryKey())
			{
				filteredCandidateColumnsNames.add(col.getName());
			}
		}
		
		filteredCandidateColumnsNames.sort(new AlphanumComparator<>());
		
		targetColumn.setPossibleValues(filteredCandidateColumnsNames);
		
	}

*/
	
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
