package cz.cas.mbu.cytimeseries.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.util.ListMultipleSelection;
import org.slf4j.LoggerFactory;

import cz.cas.mbu.cytimeseries.DataSeriesStorageProvider;
import cz.cas.mbu.cytimeseries.DataSeriesException;

public class TimeSeriesTunableParams implements TunableValidator{
	@Tunable
	public CyNetwork network;

	@Tunable(description="Name")
	public String name;
	
	@Tunable(description="Columns")
	public ListMultipleSelection<String> dataColumns;
		
	
	@Tunable(description="Time points for the series.", tooltip = "A comma separated list. Matlab notation (1:5, 1:0.5:15) supported.")
	public String timePointsString;
	
	public <TARGET_CLASS extends CyIdentifiable> TimeSeriesTunableParams(CyApplicationManager applicationManager, DataSeriesStorageProvider<TARGET_CLASS> currentParams)
	{
		network = applicationManager.getCurrentNetwork();
		if(network == null)
		{
			throw new DataSeriesException("No active network");
		}

		dataColumns = new ListMultipleSelection<>();
		
		if(currentParams != null)
		{
			ShowColumnsForClass(currentParams.getTargetClass());			
		}
		else
		{
			ShowColumnsForClass(CyNode.class);
		}
		
		if(currentParams != null)
		{
			dataColumns.setSelectedValues(currentParams.getDataColumns());
			timePointsString = MatlabSyntaxNumberList.stringFromList(currentParams.getTimePoints());
		}
		else
		{
			//select all by default
			dataColumns.setSelectedValues(dataColumns.getPossibleValues());			
		}
	}
	
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
		
		dataColumns.setPossibleValues(filteredCandidateColumnsNames);
		//select all by default
		dataColumns.setSelectedValues(dataColumns.getPossibleValues());			
		
	}

	@Override
	public ValidationState getValidationState(final Appendable errMsg) {
		try { 
			List<Double> timePoints = null;
			
			ValidationState validationState = ValidationState.OK;
			
			if(name.length() < 1)
			{
				errMsg.append("You have to enter a name for the series");
				validationState = ValidationState.INVALID;
			}
			
			if(dataColumns.getSelectedValues().size() < 1)
			{
				errMsg.append("You have to select at least one column.\n");
				validationState = ValidationState.INVALID;
			}
			else if(dataColumns.getSelectedValues().size() < 3)
			{
				errMsg.append("You are creating a series with less than 3 time points.\n");
				if(validationState == ValidationState.OK)
				{
					validationState = ValidationState.REQUEST_CONFIRMATION;					
				}
			}
			
			try {
				timePoints = MatlabSyntaxNumberList.listFromString(timePointsString);
			}
			catch (NumberFormatException ex)
			{
				errMsg.append("Incorrect time points format:" + ex.getMessage());
				validationState = ValidationState.INVALID;
			}
			
			if(timePoints != null && timePoints.size() != dataColumns.getSelectedValues().size())
			{
				errMsg.append("There must be as many time points as there are data columns. Now " + timePoints.size() + " time points but " +dataColumns.getSelectedValues().size()+ " columns\n");
				validationState = ValidationState.INVALID;
			}
			return validationState;
		} catch(IOException ex)
		{
			LoggerFactory.getLogger(TimeSeriesTunableParams.class).error("Could not append to error message", ex);
			return ValidationState.INVALID;
		}
	}

	public <TARGET_CLASS extends CyIdentifiable> void applyToSeries(DataSeriesStorageProvider<TARGET_CLASS> timeSeries)
	{
		timeSeries.setName(name);
		timeSeries.setSourceType(TimeSeriesSourceType.SeparateColumns);
		timeSeries.setDataColumns(dataColumns.getSelectedValues());
		try {
			timeSeries.setTimePoints(MatlabSyntaxNumberList.listFromString(timePointsString));
		} catch (NumberFormatException ex)
		{
			LoggerFactory.getLogger(TimeSeriesTunableParams.class).error("Error parsing time points string - should have been caught by validation", ex);
		}
	}
	
}
