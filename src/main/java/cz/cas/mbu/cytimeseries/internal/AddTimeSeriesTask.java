package cz.cas.mbu.cytimeseries.internal;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.ContainsTunables;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.TunableValidator.ValidationState;
import org.cytoscape.work.util.ListSingleSelection;

import cz.cas.mbu.cytimeseries.TimeSeries;
import cz.cas.mbu.cytimeseries.TimeSeriesManager;

public class AddTimeSeriesTask extends AbstractTimeSeriesRelatedTask implements TunableValidator{

	@Tunable(description = "Attach the time series to:")
	public ListSingleSelection<TargetClassInfo> targetClass;

	@ContainsTunables
	public TimeSeriesTunableParams params;
	
	
	public AddTimeSeriesTask(CyApplicationManager cyApplicationManager, TimeSeriesManager manager) {
		super(cyApplicationManager, manager);
		targetClass = new ListSingleSelection<>(new TargetClassInfo("Nodes", CyNode.class), new TargetClassInfo("Edges", CyEdge.class));
		params = new TimeSeriesTunableParams(cyApplicationManager, null);
	}


	@ProvidesTitle
	public String getTitle()
	{
		return "Adding time series for network " + (network == null ? "<INVALID>" : network.getRow(network).get(CyNetwork.NAME, String.class)); 
	}
	

	@Override
	public void run(TaskMonitor tm) throws Exception {
		//A bit hackish here, but I have no way to have a proper type params available... Long live type erasure.
		TimeSeries series = manager.createTimeSeries(network, targetClass.getSelectedValue().getTargetClass());
		params.applyToSeries(series);
	}

	
	
	@Override
	public ValidationState getValidationState(Appendable errMsg) {
		return params.getValidationState(errMsg);
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
