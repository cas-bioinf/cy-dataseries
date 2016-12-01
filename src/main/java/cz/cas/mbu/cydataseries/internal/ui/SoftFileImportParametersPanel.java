package cz.cas.mbu.cydataseries.internal.ui;

import java.io.StringReader;
import java.util.List;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import cz.cas.mbu.cydataseries.dataimport.PreImportResults;
import cz.cas.mbu.cydataseries.internal.dataimport.DataSeriesImportParameters;
import cz.cas.mbu.cydataseries.internal.dataimport.ImportHelper;

public class SoftFileImportParametersPanel extends JPanel {

	private final IndexImportOptionsPanel indexImportOptionsPanel;
	private final SelectColumnsToImportPanel columnsToImportPanel;
	private final ImportPreviewPanel previewPanel;
	
	private List<List<String>> rawTableData;
	private PreImportResults lastPreviewResults;
	
	private final Logger userLogger = Logger.getLogger(CyUserLog.NAME); 
	
	/**
	 * Create the panel.
	 */
	public SoftFileImportParametersPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		indexImportOptionsPanel = new IndexImportOptionsPanel();
		add(indexImportOptionsPanel, "2, 2, fill, fill");
		indexImportOptionsPanel.addChangedListener(evt -> updatePreview());
		
		columnsToImportPanel = new SelectColumnsToImportPanel();
		add(columnsToImportPanel, "4, 2, fill, fill");
		columnsToImportPanel.addChangeListener(evt -> updatePreview());
		
		previewPanel = new ImportPreviewPanel();
		add(previewPanel, "2, 4, 3, 1, fill, fill");

	}
	
	protected void updatePreview()
	{
		try {
			DataSeriesImportParameters dataSeriesImportParameters = getDataSeriesImportParameters();
			lastPreviewResults = ImportHelper.preImportFromArray(rawTableData, dataSeriesImportParameters, false /*strict*/);
			previewPanel.updatePreview(lastPreviewResults, dataSeriesImportParameters, false, false);
		}
		catch (Exception ex)
		{
			userLogger.error("Error creating import preview", ex);
			previewPanel.showError(ex.getClass().getSimpleName() + ": " + ex.getMessage());			
		}
	}
	

	public DataSeriesImportParameters getDataSeriesImportParameters()
	{
		DataSeriesImportParameters value = new DataSeriesImportParameters();
		value.setIndexSource(indexImportOptionsPanel.getIndexSource());
		value.setManualIndexValues(indexImportOptionsPanel.getManualIndexValues());
		value.setImportRowNames(true);
		value.setImportAllColumns(columnsToImportPanel.isImportAllColumns());
		value.setImportedColumnIndices(columnsToImportPanel.getImportedColumnIndices());		
		return value;
	}
}
