package cz.cas.mbu.cydataseries.internal.dataimport;

import org.cytoscape.model.SUIDFactory;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.Tunable;

import cz.cas.mbu.cydataseries.DataSeries;
import cz.cas.mbu.cydataseries.dataimport.PreImportResults;
import cz.cas.mbu.cydataseries.internal.dataimport.SoftFile.SoftTable;


public class ImportSoftFileTask extends AbstractImportTask {
	
	@Tunable(gravity = AbstractImportTask.IMPORT_PARAMS_GRAVITY)
	public SoftFileImportParameters importParameters;
			
	public ImportSoftFileTask(CyServiceRegistrar registrar) {
		super(registrar);
		this.importParameters = new SoftFileImportParameters();
	}
	
	@ProvidesTitle
	public String getTitle()
	{
		return "Adding data series from SOFT file."; 
	}

	protected void tryImportSeries() throws Exception
	{
		SoftTable table = importParameters.getSelectedTable();
		PreImportResults preImportResults = ImportHelper.preImportFromArrayAndIndex(table.getColumnNames(), table.getContents(), importParameters.getDataSeriesImportParameters(), true /* strict */);
		DataSeries<?, ?> ds = provider.getSelectedValue().getProvider().importDataDataSeries(name, SUIDFactory.getNextSUID(), preImportResults);
		importedDS = ds;
	}
		
}
