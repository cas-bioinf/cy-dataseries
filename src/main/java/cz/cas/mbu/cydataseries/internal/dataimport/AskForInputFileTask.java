package cz.cas.mbu.cydataseries.internal.dataimport;

import java.io.File;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

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
	private final BiFunction<File, StringBuilder, ValidationState> validator;
	
	public AskForInputFileTask(String title, Consumer<File> fileTarget) {
		super();
		this.title = title;
		this.fileTarget = fileTarget;
		validator = null;
	}
	
	

	public AskForInputFileTask(String title, Consumer<File> fileTarget,
			BiFunction<File, StringBuilder, ValidationState> validator) {
		super();
		this.title = title;
		this.fileTarget = fileTarget;
		this.validator = validator;
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
		if(validator != null)
		{
			return validator.apply(inputFile, errMsg);
		}
		else 
		{
			return ValidationState.OK;
		}
	}

}
