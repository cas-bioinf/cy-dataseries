package cz.cas.mbu.cydataseries.internal.tasks;

import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import cz.cas.mbu.cydataseries.DataSeries;
import cz.cas.mbu.cydataseries.DataSeriesManager;

public class RemoveDataSeriesTask extends AbstractValidatedTask {
	
	@Tunable(description="Series to remove")
	public ListSingleSelection<DataSeries<?, ?>> dataSeries;
	
	private final DataSeriesManager dataSeriesManager;
	
	public RemoveDataSeriesTask(DataSeriesManager dataSeriesManager) {
		this.dataSeriesManager = dataSeriesManager;
		dataSeries = new ListSingleSelection<>(dataSeriesManager.getAllDataSeries());
	}
	
	
	@ProvidesTitle
	public String getTitle()
	{
		return "Remove data series";
	}
	
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		dataSeriesManager.unregisterDataSeries(dataSeries.getSelectedValue());		
	}

	@Override
	public ValidationState getValidationState(StringBuilder errMsg) {
		errMsg.append("Are you sure you want to remove data series '" + dataSeries.getSelectedValue().getName() + "'?");
		return ValidationState.REQUEST_CONFIRMATION;
	}
	
}
