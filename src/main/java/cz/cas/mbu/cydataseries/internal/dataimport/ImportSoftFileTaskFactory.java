package cz.cas.mbu.cydataseries.internal.dataimport;

import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import cz.cas.mbu.cydataseries.DataSeries;
import cz.cas.mbu.cydataseries.DataSeriesManager;
import cz.cas.mbu.cydataseries.dataimport.DataSeriesImportManager;


public class ImportSoftFileTaskFactory extends AbstractTaskFactory {

	private final CyServiceRegistrar registrar;

	public ImportSoftFileTaskFactory(CyServiceRegistrar registrar) {
		super();
		this.registrar = registrar;
	}

	public TaskIterator createTaskIterator(Class<? extends DataSeries<?, ?>> preferredClass) {
		ImportSoftFileTask importTask = new ImportSoftFileTask(registrar);
		if(preferredClass != null)
		{
			importTask.setPreferredProvider(preferredClass);			
		}

		ChooseSoftTableTask chooseTableTask = new ChooseSoftTableTask(selectedTable -> { 
				importTask.importParameters.setSelectedTable(selectedTable);
			});
		
		AskForInputFileTask inputFileTask = new AskForInputFileTask("Choose input file", 
				file -> 
				{
					try (Stream<String> lines = Files.lines(file.toPath())) {
						SoftFileImporter importer = new SoftFileImporter();
						importer.parseLines(lines);
						SoftFile result = importer.getResult();
						if(result.getTables().isEmpty())
						{
							throw new IllegalArgumentException("No data tables recognized in the SOFT file");
						}
						else 
						{
							chooseTableTask.setSoftFile(result);
						}						
					}
					catch(IOException ex)
					{
						throw new IllegalArgumentException("Error reading file: " + ex.getMessage(), ex);
					}
					
				}
				);
		return new TaskIterator(inputFileTask, chooseTableTask, importTask);		
	}
	
	
	@Override
	public TaskIterator createTaskIterator() {
		return createTaskIterator(null);
	}

}
