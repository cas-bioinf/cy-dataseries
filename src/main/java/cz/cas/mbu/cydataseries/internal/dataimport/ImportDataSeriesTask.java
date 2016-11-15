package cz.cas.mbu.cydataseries.internal.dataimport;

import java.io.FileReader;
import java.util.Optional;
import java.util.stream.Collectors;

import org.cytoscape.model.SUIDFactory;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import cz.cas.mbu.cydataseries.DataSeries;
import cz.cas.mbu.cydataseries.DataSeriesException;
import cz.cas.mbu.cydataseries.DataSeriesManager;
import cz.cas.mbu.cydataseries.TimeSeries;
import cz.cas.mbu.cydataseries.dataimport.DataSeriesImportManager;
import cz.cas.mbu.cydataseries.dataimport.DataSeriesImportProvider;
import cz.cas.mbu.cydataseries.dataimport.PreImportResults;
import cz.cas.mbu.cydataseries.internal.tasks.AbstractValidatedTask;


public class ImportDataSeriesTask extends AbstractValidatedTask {

	@Tunable(description="Name", required = true)
	public String name = "";
	
	@Tunable(description="Series Type", required = true)
	public ListSingleSelection<ProviderDisplay> provider;
	
	@Tunable
	public ImportParameters importParameters;
	
	private final DataSeriesManager dataSeriesManager;
		
	private DataSeries<?,?> importedDS;
	
	public ImportDataSeriesTask(DataSeriesManager dataSeriesManager, DataSeriesImportManager importManager) {
		super();
		this.importParameters = new ImportParameters();
		this.dataSeriesManager = dataSeriesManager;
		
		this.provider = new ListSingleSelection<>(importManager.getAllImportProviders().stream()
				.map(x -> new ProviderDisplay(x))
				.collect(Collectors.toList()));
		
		Optional<ProviderDisplay> timeSeriesDisplay = provider.getPossibleValues().stream()
				.filter(x -> 
					x.getProvider().getImportedClass() != null && TimeSeries.class.isAssignableFrom(x.getProvider().getImportedClass()))
				.findAny();
		
		if(timeSeriesDisplay.isPresent())
		{
			provider.setSelectedValue(timeSeriesDisplay.get());
		}
	}

	public void setPreferredProvider(Class<? extends DataSeries<?, ?>> preferredClass)
	{
		if (preferredClass != null)
		{
			ProviderDisplay bestMatch = null;
			for(int index = 0; index < provider.getPossibleValues().size(); index++)
			{
				ProviderDisplay display = provider.getPossibleValues().get(index);
				Class<? extends DataSeries<?, ?>> importedClass = display.getProvider().getImportedClass();
				if (preferredClass.equals(importedClass)){
					//exact match, do not search any longer
					bestMatch = display;
					break;
				}
				else if (preferredClass.isAssignableFrom(importedClass) && bestMatch == null) {
					//inexact match, set only if not better match found yet and continue searching
					bestMatch = display;
				}
			}
			
			if(bestMatch != null)
			{
				provider.setSelectedValue(bestMatch);
			}
		}
	}
	
	@ProvidesTitle
	public String getTitle()
	{
		return "Adding data series."; 
	}

	@Override
	public void run(TaskMonitor tm) throws Exception {
		//DS is loaded during validation to let the user modify the options immediately after an import failure
		if(importedDS == null)
		{
			throw new DataSeriesException("Invalid import task state: series was not imported, although validation passed.");
		}
		dataSeriesManager.registerDataSeries(importedDS);
	}

	protected void tryImportSeries() throws Exception
	{
		try (FileReader inputReader = new FileReader(importParameters.getFile())) {
			PreImportResults preImportResults = ImportHelper.preImport(inputReader, importParameters, true /* strict */);
			DataSeries<?, ?> ds = provider.getSelectedValue().getProvider().importDataDataSeries(name, SUIDFactory.getNextSUID(), preImportResults);
			importedDS = ds;
		}
	}
	
	@Override
	public ValidationState getValidationState(StringBuilder errMsg) {
		if (provider == null) {
			errMsg.append("You have to select a series type");
			return ValidationState.INVALID;
		}
		
		if(importParameters.getFile() == null || !importParameters.getFile().exists())
		{
			errMsg.append("You have to select an existing file");
			return ValidationState.INVALID;
		}
		
		try {
			tryImportSeries();
		}
		catch (Exception ex) {
			errMsg.append(ex.getMessage());
			return ValidationState.INVALID;
		}			
		
		if(name.isEmpty()) {
			errMsg.append("Are you sure you do not want to provide a name?");
			return ValidationState.REQUEST_CONFIRMATION;
		}
		else if(name.length() < 3) {
			errMsg.append("Are you sure you want to use such a short name?");
			return ValidationState.REQUEST_CONFIRMATION;
		}

		return ValidationState.OK;
	}

	public static class ProviderDisplay {
		private final DataSeriesImportProvider provider;

		public ProviderDisplay(DataSeriesImportProvider provider) {
			super();
			this.provider = provider;
		}

		public DataSeriesImportProvider getProvider() {
			return provider;
		}

		@Override
		public String toString() {
			return provider.getDescription();
		}
	}

}
