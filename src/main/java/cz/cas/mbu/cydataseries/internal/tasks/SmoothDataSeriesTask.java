package cz.cas.mbu.cydataseries.internal.tasks;

import java.util.Arrays;
import java.util.List;

import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListMultipleSelection;
import org.cytoscape.work.util.ListSingleSelection;

import com.google.common.primitives.Doubles;

import cz.cas.mbu.cydataseries.DataSeries;
import cz.cas.mbu.cydataseries.DataSeriesManager;
import cz.cas.mbu.cydataseries.SmoothingService;
import cz.cas.mbu.cydataseries.TimeSeries;
import cz.cas.mbu.cydataseries.internal.dataimport.MatlabSyntaxNumberList;

public class SmoothDataSeriesTask extends AbstractValidatedTask {
	
	@Tunable(description="Name of the resulting series")
	public String resultName;
	
	@Tunable(description="Series to smooth")
	public ListSingleSelection<TimeSeries> timeSeries;
	
	@Tunable(description="Estimate in the same time points as the original serie(s)")
	public boolean keepSourcePoints = false;
	
	@Tunable(description="Time points to estimate the data.\nComma separated, supports Matlab notation (e.g. 1,2,3:5,10:2:20)",
			dependsOn="keepSourcePoints=false")
	public String estimationTimePoints;

	@Tunable(description="Kernel bandwidth")
	public double bandwidth = 1;
	
	private final DataSeriesManager dataSeriesManager;
	private final SmoothingService smoothingService;
	
	public SmoothDataSeriesTask(DataSeriesManager dataSeriesManager, SmoothingService smoothingService) {
		this.dataSeriesManager = dataSeriesManager;
		this.smoothingService = smoothingService;
		timeSeries = new ListSingleSelection<>(dataSeriesManager.getDataSeriesByType(TimeSeries.class));
	}
	
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		TimeSeries result;
		double[] resultTimePoints;
		TimeSeries selectedSeries = timeSeries.getSelectedValue();
		if(!keepSourcePoints)
		{
			List<Double> timePointsList = MatlabSyntaxNumberList.listFromString(estimationTimePoints);			
			resultTimePoints = Doubles.toArray(timePointsList);
		}
		else 
		{
			resultTimePoints = Arrays.copyOf(selectedSeries.getIndexArray(), selectedSeries.getIndexCount());
		}
		
		result = smoothingService.linearKernelSmoothing(selectedSeries, resultTimePoints, bandwidth, resultName);		
		dataSeriesManager.registerDataSeries(result);
	
	}

	@Override
	public ValidationState getValidationState(StringBuilder errMsg) {
		if(resultName.isEmpty())
		{
			errMsg.append("You have to specify a name for the new time series.");
			return ValidationState.INVALID;
		}
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
		return ValidationState.OK;
	}
		
}
