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
import cz.cas.mbu.cytimeseries.DataSeries;
import cz.cas.mbu.cytimeseries.DataSeriesManager;

public class AskForInputFileTask extends AbstractTask implements TunableValidator{

	@Tunable(description="Data file", required = true, params="input=true;fileCategory=table")
	public File inputFile = null;
	
	private final String title;
	
	public AskForInputFileTask(String title) {
		super();
		this.title = title;
	}


	@ProvidesTitle
	public String getTitle()
	{
		return title; 
	}
	

	@Override
	public void run(TaskMonitor tm) throws Exception {
	}

	
	
	@Override
	public ValidationState getValidationState(Appendable errMsg) {
		try{
			if(inputFile == null || !inputFile.exists())
			{
				errMsg.append("You have to select an input file");
				return ValidationState.INVALID;
			}
		} catch (IOException ex)
		{
			return ValidationState.INVALID;
		}
		return ValidationState.OK;
	}




}
