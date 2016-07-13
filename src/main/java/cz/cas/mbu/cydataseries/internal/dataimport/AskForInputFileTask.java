package cz.cas.mbu.cydataseries.internal.dataimport;

import java.io.File;
import java.util.function.Consumer;

import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import cz.cas.mbu.cydataseries.internal.tasks.AbstractValidatedTask;

/**
 * Dialog for input file.
 */
public class AskForInputFileTask extends AbstractValidatedTask {

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
	public ValidationState getValidationState(StringBuilder errMsg) {
		if(inputFile == null || !inputFile.exists()) {
			errMsg.append("You have to select an input file");
			return ValidationState.INVALID;
		}
		return ValidationState.OK;
	}

}
