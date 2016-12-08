package cz.cas.mbu.cydataseries.internal.dataimport;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TunableValidator.ValidationState;

import cz.cas.mbu.cydataseries.DataSeries;
import cz.cas.mbu.cydataseries.DataSeriesManager;
import cz.cas.mbu.cydataseries.dataimport.DataSeriesImportManager;


public class ImportSoftFileTaskFactory extends AbstractTaskFactory {

	private final CyServiceRegistrar registrar;

	private static final int MAX_SAFE_FILE_SIZE = 120 * 1024 * 1024; //120MB
	
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
		
		Consumer<File> selectedFileConsumer = file -> 
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
			
		};
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
				selectedFileConsumer
				, fileValidator
				);
		return new TaskIterator(inputFileTask, chooseTableTask, importTask);		
	}
	
	
	@Override
	public TaskIterator createTaskIterator() {
		return createTaskIterator(null);
	}

}
