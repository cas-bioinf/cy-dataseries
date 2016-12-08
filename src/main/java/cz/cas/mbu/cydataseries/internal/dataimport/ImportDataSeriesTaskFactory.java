package cz.cas.mbu.cydataseries.internal.dataimport;

import java.io.File;
import java.util.function.BiFunction;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TunableValidator.ValidationState;

import cz.cas.mbu.cydataseries.DataSeries;
import cz.cas.mbu.cydataseries.DataSeriesManager;
import cz.cas.mbu.cydataseries.dataimport.DataSeriesImportManager;


public class ImportDataSeriesTaskFactory extends AbstractTaskFactory {

	private final CyServiceRegistrar registrar;
	
	private static final int MAX_SAFE_FILE_SIZE = 64 * 1024 * 1024;

	public ImportDataSeriesTaskFactory(CyServiceRegistrar registrar) {
		super();
		this.registrar = registrar;
	}

	public TaskIterator createTaskIterator(Class<? extends DataSeries<?, ?>> preferredClass) {
		ImportDataSeriesTask importTask = new ImportDataSeriesTask(registrar);
		if(preferredClass != null)
		{
			importTask.setPreferredProvider(preferredClass);			
		}
		BiFunction<File, StringBuilder, ValidationState> fileValidator = (file, msg) ->
		{
			if(file.length() > MAX_SAFE_FILE_SIZE)
			{
				msg.append("The file you have chosen is large, and may cause Cytoscape to run out of memory.\nDue to a bug in Cytoscape this may render the program unusable and you may loose data.\nAre you sure you want to continue?");
				return ValidationState.REQUEST_CONFIRMATION;
			}
			return ValidationState.OK;
		};
		
		AskForInputFileTask inputFileTask = new AskForInputFileTask("Choose input file", 
				file -> importTask.importParameters.setFile(file)
				, fileValidator
				);
		return new TaskIterator(inputFileTask, importTask);		
	}
	
	
	@Override
	public TaskIterator createTaskIterator() {
		return createTaskIterator(null);
	}

}
