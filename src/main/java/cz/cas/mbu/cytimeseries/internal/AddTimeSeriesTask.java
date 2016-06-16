package cz.cas.mbu.cytimeseries.internal;

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
import cz.cas.mbu.cytimeseries.DataSeries;
import cz.cas.mbu.cytimeseries.DataSeriesManager;

public class AddTimeSeriesTask extends AbstractTask implements TunableValidator{

	@Tunable(description="Name", required = true)
	public String name = "";
	
	@Tunable(description="Data file", required = true, params="input=true;fileCategory=table")
	public File inputFile = null;
	
	private final DataSeriesManager dataSeriesManager;
	private final DataSeriesStorageProvider storageProvider;
	



	public AddTimeSeriesTask(DataSeriesManager dataSeriesManager, DataSeriesStorageProvider storageProvider) {
		super();
		this.dataSeriesManager = dataSeriesManager;
		this.storageProvider = storageProvider;
	}


	@ProvidesTitle
	public String getTitle()
	{
		return "Adding time series."; 
	}
	

	@Override
	public void run(TaskMonitor tm) throws Exception {
		try {
			DataSeries<?, ?> ds = storageProvider.loadDataSeries(inputFile, name, SUIDFactory.getNextSUID());
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
			if(inputFile == null || !inputFile.exists())
			{
				errMsg.append("You have to select an input file");
				return ValidationState.INVALID;
			}
			if(name.length() < 3)
			{
				errMsg.append("Are you sure you do not want to provide a name?");
				return ValidationState.REQUEST_CONFIRMATION;
			}
		} catch (IOException ex)
		{
			return ValidationState.INVALID;
		}
		return ValidationState.OK;
	}




}
