package cz.cas.mbu.cydataseries.internal.tasks;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;
import org.jfree.data.time.TimeSeriesTableModel;

import com.google.common.primitives.Doubles;

import cz.cas.mbu.cydataseries.DataSeriesManager;
import cz.cas.mbu.cydataseries.SmoothingService;
import cz.cas.mbu.cydataseries.TimeSeries;
import cz.cas.mbu.cydataseries.internal.dataimport.MatlabSyntaxNumberList;
import cz.cas.mbu.cydataseries.internal.ui.SmoothingPreviewPanel;
import cz.cas.mbu.cydataseries.internal.ui.UIUtils;

public class SmoothInteractivePerformTask extends AbstractValidatedTask {
	
	@Tunable(description="Name of the resulting series")
	public String resultName;
	

	private final CyServiceRegistrar registrar;
	
	private final TimeSeries sourceTimeSeries;
	private final double[] estimateX;
	private final double bandwidth;
		
	
	public SmoothInteractivePerformTask(CyServiceRegistrar registrar, TimeSeries sourceTimeSeries,
			double[] estimateX, double bandwidth) {
		super();
		this.registrar = registrar;
		this.sourceTimeSeries = sourceTimeSeries;
		this.estimateX = estimateX;
		this.bandwidth = bandwidth;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		
		SmoothingService smoothingService = registrar.getService(SmoothingService.class);
		TimeSeries smoothedSeries = smoothingService.linearKernelSmoothing(sourceTimeSeries, estimateX, bandwidth, resultName, smoothingService.getDefaultRowGrouping(sourceTimeSeries));
		
		registrar.getService(DataSeriesManager.class).registerDataSeries(smoothedSeries);		
	}

	@Override
	public ValidationState getValidationState(StringBuilder errMsg) {
		if(resultName.isEmpty())
		{
			errMsg.append("You have to specify a name for the new time series.");
			return ValidationState.INVALID;
		}
		return ValidationState.OK;
	}
		
}
