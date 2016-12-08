package cz.cas.mbu.cydataseries.internal.tasks;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.ContainsTunables;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListMultipleSelection;
import org.cytoscape.work.util.ListSingleSelection;

import com.google.common.primitives.Doubles;

import cz.cas.mbu.cydataseries.DataSeries;
import cz.cas.mbu.cydataseries.DataSeriesManager;
import cz.cas.mbu.cydataseries.MappingManipulationService;
import cz.cas.mbu.cydataseries.SmoothingService;
import cz.cas.mbu.cydataseries.TimeSeries;
import cz.cas.mbu.cydataseries.internal.dataimport.MatlabSyntaxNumberList;

public class SmoothDataSeriesTask extends AbstractValidatedTask {
		
	@Tunable(description="Series to smooth")
	public ListSingleSelection<TimeSeries> timeSeries;
	
	@Tunable(description="Estimate in the same time points as the original serie(s)")
	public boolean keepSourcePoints = false;
	
	@Tunable(description="Time points to estimate the data.\nComma separated, supports Matlab notation (e.g. 1,2,3:5,10:2:20)",
			dependsOn="keepSourcePoints=false")
	public String estimationTimePoints;

	@Tunable(description="Combine rows with the same name together")
	public boolean combineRows;
	
	
	@Tunable(description="Kernel bandwidth")
	public double bandwidth = 1;
	
	@ContainsTunables
	public SmoothingOutputParameters outputParameters;
	
	private final DataSeriesManager dataSeriesManager;
	private final SmoothingService smoothingService;
	private final CyServiceRegistrar registrar;
	
	public SmoothDataSeriesTask(CyServiceRegistrar registrar) {
		this.dataSeriesManager = registrar.getService(DataSeriesManager.class);
		this.smoothingService = registrar.getService(SmoothingService.class);
		this.registrar = registrar;
		timeSeries = new ListSingleSelection<>(dataSeriesManager.getDataSeriesByType(TimeSeries.class));
		outputParameters = new SmoothingOutputParameters();
	}
	
	@ProvidesTitle
	public String getTitle()
	{
		return "Smooth a data series";
	}
	
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		TimeSeries smoothedSeries;
		double[] resultTimePoints;
		TimeSeries sourceTimeSeries = timeSeries.getSelectedValue();
		if(!keepSourcePoints)
		{
			List<Double> timePointsList = MatlabSyntaxNumberList.listFromString(estimationTimePoints);			
			resultTimePoints = Doubles.toArray(timePointsList);
		}
		else 
		{
			resultTimePoints = Arrays.copyOf(sourceTimeSeries.getIndexArray(), sourceTimeSeries.getIndexCount());
		}
		
		smoothedSeries = smoothingService.linearKernelSmoothing(sourceTimeSeries, resultTimePoints, bandwidth, outputParameters.resultName);		
		dataSeriesManager.registerDataSeries(smoothedSeries);
		
		Map<String, List<Integer>> rowGrouping;
		if(combineRows)
		{
			rowGrouping = smoothingService.getDefaultRowGrouping(sourceTimeSeries);
		}
		else
		{
			rowGrouping = null;
		}
		
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
			
	}

	@Override
	public ValidationState getValidationState(StringBuilder errMsg) {
	
		if(timeSeries.getSelectedValue() == null)
		{
			errMsg.append("You have to select an input time series");
			return ValidationState.INVALID;
		}
		if(bandwidth <= 0)
		{
			errMsg.append("Bandwidth has to be positive");
			return ValidationState.INVALID;
		}
		if(!keepSourcePoints)
		{
			try
			{
				List<Double> timePoints = MatlabSyntaxNumberList.listFromString(estimationTimePoints);
				if(timePoints.isEmpty())
				{
					errMsg.append("You have to specify at least one valid estimation time points");
					return ValidationState.INVALID;
				}
			}
			catch (NumberFormatException ex)
			{
				errMsg.append("Cannot parse the estimation time points: " + ex.getMessage());
				return ValidationState.INVALID;
			}
			
		}
		
		ValidationState outputValidation = outputParameters.getValidationState(timeSeries.getSelectedValue(), registrar, errMsg); 
		
		return ValidationState.OK;
	}
		
}
