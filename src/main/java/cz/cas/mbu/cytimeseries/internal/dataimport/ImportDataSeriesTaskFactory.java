package cz.cas.mbu.cytimeseries.internal.dataimport;

import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import cz.cas.mbu.cytimeseries.DataSeriesManager;
import cz.cas.mbu.cytimeseries.DataSeriesStorageProvider;

public class ImportDataSeriesTaskFactory extends AbstractTaskFactory {

	private final DataSeriesManager dataSeriesManager;
	private final DataSeriesStorageProvider storageProvider;

	
	
	public ImportDataSeriesTaskFactory(DataSeriesManager dataSeriesManager, DataSeriesStorageProvider storageProvider) {
		super();
		this.dataSeriesManager = dataSeriesManager;
		this.storageProvider = storageProvider;
	}



	@Override
	public TaskIterator createTaskIterator() {
		AskForInputFileTask inputFileTask = new AskForInputFileTask("Choose input file");
		ImportDataSeriesTask importTask = new ImportDataSeriesTask(inputFileTask, dataSeriesManager, storageProvider);
		return new TaskIterator(inputFileTask, importTask);
	}

}
