package cz.cas.mbu.cydataseries.internal.dataimport;

import java.nio.file.attribute.UserPrincipalLookupService;
import java.security.GeneralSecurityException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import cz.cas.mbu.cydataseries.DataSeries;
import cz.cas.mbu.cydataseries.DataSeriesException;
import cz.cas.mbu.cydataseries.DataSeriesManager;
import cz.cas.mbu.cydataseries.TimeSeries;
import cz.cas.mbu.cydataseries.dataimport.DataSeriesImportManager;
import cz.cas.mbu.cydataseries.dataimport.DataSeriesImportProvider;
import cz.cas.mbu.cydataseries.internal.tasks.AbstractValidatedTask;
import cz.cas.mbu.cydataseries.internal.tasks.MapColumnTask;

/**
 * Handling of parameters common to {@link ImportSoftFileTask} and {@link ImportDataSeriesTask}.
 * Note that counterintuitively, the actual import happens during the validation stage (this lets us signal errors to user and
 * let them correct their import settings instead of having to start all over).
 * @author MBU
 *
 */
public abstract class AbstractImportTask extends AbstractValidatedTask {

	private final Logger logger = Logger.getLogger(AbstractImportTask.class);
	
	@Tunable(description = "Name", required = true, gravity = 10, groups="Basic parameters")
	public String name = "";


	@Tunable(description = "Series Type", required = true, gravity = 20, groups="Basic parameters")
	public ListSingleSelection<ProviderDisplay> provider;
	
	@Tunable(description = "Map the series to the current network after import", gravity = 30, groups="Basic parameters")
	public boolean mapAfterImport = true;
	
	protected final DataSeriesManager dataSeriesManager;
	protected final CyServiceRegistrar registrar;
	protected DataSeries<?,?> importedDS;

	public static final int IMPORT_PARAMS_GRAVITY = 40;
	
	public AbstractImportTask(CyServiceRegistrar registrar) {
		super();
		this.registrar = registrar;
		this.dataSeriesManager = registrar.getService(DataSeriesManager.class);
		
		DataSeriesImportManager importManager = registrar.getService(DataSeriesImportManager.class);
		
		this.provider = new ListSingleSelection<>(importManager.getAllImportProviders().stream()
				.map(x -> new ProviderDisplay(x))
				.collect(Collectors.toList()));
		
		Optional<ProviderDisplay> timeSeriesDisplay = provider.getPossibleValues().stream()
				.filter(x -> 
					x.getProvider().getImportedClass() != null && TimeSeries.class.isAssignableFrom(x.getProvider().getImportedClass()))
				.findAny();
		
		if(timeSeriesDisplay.isPresent())
		{
			provider.setSelectedValue(timeSeriesDisplay.get());
		}
		
	}

	public void setPreferredProvider(Class<? extends DataSeries<?, ?>> preferredClass) {
		if (preferredClass != null)
		{
			ProviderDisplay bestMatch = null;
			for(int index = 0; index < provider.getPossibleValues().size(); index++)
			{
				ProviderDisplay display = provider.getPossibleValues().get(index);
				Class<? extends DataSeries<?, ?>> importedClass = display.getProvider().getImportedClass();
				if (preferredClass.equals(importedClass)){
					//exact match, do not search any longer
					bestMatch = display;
					break;
				}
				else if (preferredClass.isAssignableFrom(importedClass) && bestMatch == null) {
					//inexact match, set only if not better match found yet and continue searching
					bestMatch = display;
				}
			}
			
			if(bestMatch != null)
			{
				provider.setSelectedValue(bestMatch);
			}
		}
	}
	
	@Override
	public void run(TaskMonitor tm) throws Exception {
		//DS is loaded during validation to let the user modify the options immediately after an import failure
		if(importedDS == null)
		{
			throw new DataSeriesException("Invalid import task state: series was not imported, although validation passed.");
		}
		dataSeriesManager.registerDataSeries(importedDS);
		if(mapAfterImport)
		{			
			MapColumnTask mapColumnTask = new MapColumnTask(registrar);
			mapColumnTask.dataSeries.setSelectedValue(importedDS);
			mapColumnTask.newColumnName = importedDS.getName();
			insertTasksAfterCurrentTask(mapColumnTask);
		}
	}
	

	protected abstract void tryImportSeries() throws Exception;
	
	@Override
	public ValidationState getValidationState(StringBuilder errMsg) {
		if (provider == null) {
			errMsg.append("You have to select a series type");
			return ValidationState.INVALID;
		}
				
		try {
			tryImportSeries();
		}
		catch (Exception ex) {
			logger.warn("Error in importing series:", ex);
			errMsg.append(ex.getMessage());
			return ValidationState.INVALID;
		}			
		
		if(name.isEmpty()) {
			errMsg.append("Are you sure you do not want to provide a name?");
			return ValidationState.REQUEST_CONFIRMATION;
		}
		else if(name.length() < 3) {
			errMsg.append("Are you sure you want to use such a short name?");
			return ValidationState.REQUEST_CONFIRMATION;
		}
	
		return ValidationState.OK;
	}

	public static class ProviderDisplay {
		private final DataSeriesImportProvider provider;
	
		public ProviderDisplay(DataSeriesImportProvider provider) {
			super();
			this.provider = provider;
		}
	
		public DataSeriesImportProvider getProvider() {
			return provider;
		}
	
		@Override
		public String toString() {
			return provider.getDescription();
		}
	}
	
}