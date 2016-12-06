package cz.cas.mbu.cydataseries.internal.dataimport;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import cz.cas.mbu.cydataseries.DataSeries;
import cz.cas.mbu.cydataseries.DataSeriesManager;
import cz.cas.mbu.cydataseries.dataimport.DataSeriesImportManager;


public class ImportDataSeriesTaskFactory extends AbstractTaskFactory {

	private final CyServiceRegistrar registrar;

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
		AskForInputFileTask inputFileTask = new AskForInputFileTask("Choose input file", 
				file -> importTask.importParameters.setFile(file)
				);
		return new TaskIterator(inputFileTask, importTask);		
	}
	
	
	@Override
	public TaskIterator createTaskIterator() {
		return createTaskIterator(null);
	}

}
