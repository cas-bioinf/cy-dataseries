package cz.cas.mbu.cytimeseries.internal.dataimport;

import java.io.File;
import java.io.IOException;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.SUIDFactory;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ContainsTunables;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskMonitor.Level;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.TunableValidator.ValidationState;
import org.cytoscape.work.util.ListSingleSelection;
import org.osgi.framework.BundleContext;

import cz.cas.mbu.cytimeseries.DataSeriesStorageProvider;
import cz.cas.mbu.cytimeseries.dataimport.DataSeriesImportProvider;
import cz.cas.mbu.cytimeseries.DataSeries;
import cz.cas.mbu.cytimeseries.DataSeriesManager;

public class ImportDataSeriesTask extends AbstractTask implements TunableValidator{

	@Tunable(description="Name", required = true)
	public String name = "";
	
	@Tunable(description="Series Type", required = true)
	public ListSingleSelection<DataSeriesImportProvider> provider;
	
	@Tunable
	public ImportParameters importParameters;
	
	private final DataSeriesManager dataSeriesManager;
	private final DataSeriesImportProvider importProvider;
	
	public ImportDataSeriesTask(AskForInputFileTask inputFileTask, DataSeriesManager dataSeriesManager, DataSeriesImportProvider importProvider) {
		super();
		this.importParameters.setFile(inputFileTask.inputFile);
		this.dataSeriesManager = dataSeriesManager;
		this.importProvider = importProvider;
	}


	@ProvidesTitle
	public String getTitle()
	{
		return "Adding time series."; 
	}
	

	@Override
	public void run(TaskMonitor tm) throws Exception {
		try {
			DataSeries<?, ?> ds = importProvider.loadDataSeries(inputFile, name, SUIDFactory.getNextSUID());
			dataSeriesManager.registerDataSeries(ds);
		}
		catch (Exception ex)
		{
			tm.showMessage(Level.ERROR, ex.getMessage());
			throw ex;
		}
	}

	
	
	@Override
	public ValidationState getValidationState(Appendable errMsg) {
		try{
			if (provider == null)
			{
				errMsg.append("You have to select a series type");
				return ValidationState.INVALID;
			}
			if(name.isEmpty())
			{
				errMsg.append("Are you sure you do not want to provide a name?");
				return ValidationState.REQUEST_CONFIRMATION;
			}
			else if(name.length() < 3)
			{
				errMsg.append("Are you sure you want to use such a short name?");
				return ValidationState.REQUEST_CONFIRMATION;
			}
		} catch (IOException ex)
		{
			return ValidationState.INVALID;
		}
		return ValidationState.OK;
	}




}
