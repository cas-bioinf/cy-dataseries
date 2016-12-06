package cz.cas.mbu.cydataseries.internal.dataimport;

import java.io.FileReader;
import java.util.Optional;
import java.util.stream.Collectors;

import org.cytoscape.model.SUIDFactory;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator.ValidationState;
import org.cytoscape.work.util.ListSingleSelection;

import cz.cas.mbu.cydataseries.DataSeries;
import cz.cas.mbu.cydataseries.DataSeriesException;
import cz.cas.mbu.cydataseries.DataSeriesManager;
import cz.cas.mbu.cydataseries.TimeSeries;
import cz.cas.mbu.cydataseries.dataimport.DataSeriesImportManager;
import cz.cas.mbu.cydataseries.dataimport.PreImportResults;


public class ImportDataSeriesTask extends AbstractImportTask {

	@Tunable(gravity = IMPORT_PARAMS_GRAVITY)
	public TabularFileImportParameters importParameters;
	
	public ImportDataSeriesTask(CyServiceRegistrar registrar) {
		super(registrar);
		this.importParameters = new TabularFileImportParameters();
	}

	@ProvidesTitle
	public String getTitle()
	{
		return "Adding data series."; 
	}	
	
	@Override
	public ValidationState getValidationState(StringBuilder errMsg) {
		if(importParameters.getFile() == null || !importParameters.getFile().exists())
		{
			errMsg.append("You have to select an existing file");
			return ValidationState.INVALID;
		}

		return super.getValidationState(errMsg);
	}

	@Override
	protected void tryImportSeries() throws Exception
	{
		try (FileReader inputReader = new FileReader(importParameters.getFile())) {
			PreImportResults preImportResults = ImportHelper.preImport(inputReader, importParameters.getFileFormatParameters(), importParameters.getDataSeriesParameters(), true /* strict */);
			DataSeries<?, ?> ds = provider.getSelectedValue().getProvider().importDataDataSeries(name, SUIDFactory.getNextSUID(), preImportResults);
			importedDS = ds;
		}
	}

}
