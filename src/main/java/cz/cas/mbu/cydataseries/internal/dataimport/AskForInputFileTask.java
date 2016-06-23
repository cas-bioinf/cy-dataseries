package cz.cas.mbu.cydataseries.internal.dataimport;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

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

import cz.cas.mbu.cydataseries.DataSeries;
import cz.cas.mbu.cydataseries.DataSeriesManager;
import cz.cas.mbu.cydataseries.DataSeriesStorageProvider;

public class AskForInputFileTask extends AbstractTask implements TunableValidator{

	@Tunable(description="Data file", required = true, params="input=true;fileCategory=table")
	public File inputFile = null;
	
	private final String title;
	private final Consumer<File> fileTarget;
	
	public AskForInputFileTask(String title, Consumer<File> fileTarget) {
		super();
		this.title = title;
		this.fileTarget = fileTarget;
	}


	@ProvidesTitle
	public String getTitle()
	{
		return title; 
	}
	

	@Override
	public void run(TaskMonitor tm) throws Exception {
		fileTarget.accept(inputFile);		
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
