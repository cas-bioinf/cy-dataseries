package cz.cas.mbu.cydataseries.internal.tasks;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import cz.cas.mbu.cydataseries.DataSeries;
import cz.cas.mbu.cydataseries.DataSeriesException;
import cz.cas.mbu.cydataseries.DataSeriesManager;
import cz.cas.mbu.cydataseries.DataSeriesStorageManager;
import cz.cas.mbu.cydataseries.DataSeriesStorageProvider;

/**
 * Exports the selected data series to an output file.
 */
public class ExportDataSeriesTask extends AbstractValidatedTask {

	@Tunable(description = "Series to export", required = true)
	public ListSingleSelection<DataSeries<?, ?>> dataSeries;
	
	@Tunable(description="Output file",required = true, params="input=false;fileCategory=table")
	public File outputFile;
	
	private final DataSeriesStorageManager dataSeriesStorageManager;
	
	public ExportDataSeriesTask(DataSeriesManager dataSeriesManager, DataSeriesStorageManager dataSeriesStorageManager) {
		dataSeries = new ListSingleSelection<>(dataSeriesManager.getAllDataSeries());
		this.dataSeriesStorageManager = dataSeriesStorageManager;
	}
	
	@ProvidesTitle
	public String getTitle()
	{
		return "Export Data series";
	}
	
	
	@Override
	protected ValidationState getValidationState(StringBuilder messageBuilder) {
		if (outputFile == null || outputFile.isDirectory()) {
			messageBuilder.append("You have to select an output file.");
			return ValidationState.INVALID;
		}
		if (!outputFile.getParentFile().exists()) {
			messageBuilder.append("The parent directory of the output file has to exist.");
			return ValidationState.INVALID;			
		}
		if (outputFile.exists()) {
			messageBuilder.append("Are you sure you want to overwrite file '" + outputFile.toString() + "'?");
			return ValidationState.REQUEST_CONFIRMATION;
		}
		return ValidationState.OK;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		DataSeries<?, ?> selectedSeries = dataSeries.getSelectedValue();
		DataSeriesStorageProvider storageProvider = dataSeriesStorageManager.getStorageProvider(selectedSeries.getClass());
		if(storageProvider == null) {
			throw new DataSeriesException("Could not export - no storage provider found for class " + selectedSeries.getClass().getName());
		} else {
			storageProvider.saveDataSeries(selectedSeries, outputFile);			
		}
	}

}
