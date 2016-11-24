package cz.cas.mbu.cydataseries.internal.tasks;

import java.util.List;
import java.util.Map;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.ContainsTunables;
import org.cytoscape.work.TaskMonitor;

import cz.cas.mbu.cydataseries.DataSeriesManager;
import cz.cas.mbu.cydataseries.MappingManipulationService;
import cz.cas.mbu.cydataseries.SmoothingService;
import cz.cas.mbu.cydataseries.TimeSeries;
import cz.cas.mbu.cydataseries.internal.ui.SmoothingPreviewPanel;

public class SmoothInteractivePerformTask extends AbstractValidatedTask {

	@ContainsTunables
	public SmoothingOutputParameters outputParameters;
	
	private final CyServiceRegistrar registrar;
	
	private final TimeSeries sourceTimeSeries;
	private final double[] estimateX;
	private final double bandwidth;
	Map<String, List<Integer>> rowGrouping;
	
	private final SmoothingPreviewPanel sourcePanel;
	

	public SmoothInteractivePerformTask(CyServiceRegistrar registrar, TimeSeries sourceTimeSeries,
			double[] estimateX, double bandwidth, Map<String, List<Integer>> rowGrouping, SmoothingPreviewPanel sourcePanel) {
		super();
		this.registrar = registrar;
		this.sourceTimeSeries = sourceTimeSeries;
		this.estimateX = estimateX;
		this.bandwidth = bandwidth;
		this.sourcePanel = sourcePanel;
		this.rowGrouping = rowGrouping;
		outputParameters = new SmoothingOutputParameters();
		outputParameters.resultName = sourceTimeSeries + "_Smooth";
	}
	

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		
		SmoothingService smoothingService = registrar.getService(SmoothingService.class);
		TimeSeries smoothedSeries = smoothingService.linearKernelSmoothing(sourceTimeSeries, estimateX, bandwidth, outputParameters.resultName, rowGrouping);
		
		registrar.getService(DataSeriesManager.class).registerDataSeries(smoothedSeries);
		
		if(outputParameters.mapResult)
		{
			MappingManipulationService manipulationService = registrar.getService(MappingManipulationService.class);					
			
			if(outputParameters.replaceMapping)
			{
				manipulationService.replaceMapping(sourceTimeSeries, smoothedSeries, rowGrouping); 
			}
			else
			{
				manipulationService.copyMapping(sourceTimeSeries, smoothedSeries, rowGrouping, outputParameters.mappingSuffix);
			}
		}
		
		registrar.unregisterAllServices(sourcePanel);
	}

	@Override
	public ValidationState getValidationState(StringBuilder errMsg) {
		return outputParameters.getValidationState(sourceTimeSeries, registrar, errMsg);
	}
		
}
