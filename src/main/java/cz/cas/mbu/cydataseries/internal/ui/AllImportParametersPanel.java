package cz.cas.mbu.cydataseries.internal.ui;

import java.io.File;
import java.io.StringReader;

import javax.swing.JPanel;
import javax.swing.JSeparator;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import cz.cas.mbu.cydataseries.dataimport.PreImportResults;
import cz.cas.mbu.cydataseries.internal.dataimport.ImportHelper;
import cz.cas.mbu.cydataseries.internal.dataimport.ImportParameters;

public class AllImportParametersPanel extends JPanel {

	private String rawPreviewData;
	private File inputFile;
	
	private FileImportOptionsPanel fileImportOptionsPanel;
	private DataSeriesImportOptionsPanel dataSeriesImportOptionsPanel;
	private ImportPreviewPanel importPreviewPanel;
	private JSeparator separator;

	private final Logger userLogger = Logger.getLogger(CyUserLog.NAME); 
	
	/**
	 * Create the panel.
	 */
	public AllImportParametersPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				RowSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				RowSpec.decode("default:grow"),}));
		
		fileImportOptionsPanel = new FileImportOptionsPanel();
		add(fileImportOptionsPanel, "2, 1, fill, fill");
		
		dataSeriesImportOptionsPanel = new DataSeriesImportOptionsPanel();
		add(dataSeriesImportOptionsPanel, "6, 1, fill, fill");
		
		separator = new JSeparator();
		add(separator, "2, 3, 5, 1");
		
		importPreviewPanel = new ImportPreviewPanel();
		add(importPreviewPanel, "2, 6, 5, 1, fill, fill");
		
		fileImportOptionsPanel.addChangedListener(evt -> updatePreview());
		dataSeriesImportOptionsPanel.addChangedListener(evt -> updatePreview());
	}
	
	public void setPreviewData(String rawData)
	{
		rawPreviewData = rawData;
		updatePreview();
	}
	
	public void setInputfile(File inputFile)
	{
		this.inputFile = inputFile;
	}
	
	protected void updatePreview()
	{
		try {
			PreImportResults preImportResults = ImportHelper.preImport(new StringReader(rawPreviewData), getImportParameters(), false /*strict*/);
			importPreviewPanel.updatePreview(preImportResults);
		}
		catch (Exception ex)
		{
			userLogger.error("Error creating import preview", ex);
			importPreviewPanel.showError(ex.getClass().getSimpleName() + ": " + ex.getMessage());			
		}
	}
	
	public ImportParameters getImportParameters()
	{
		ImportParameters value = new ImportParameters();
		value.setFile(inputFile);
		value.setPreviewData(rawPreviewData);
		value.setSeparator(fileImportOptionsPanel.getSeparator());
		value.setCommentCharacter(fileImportOptionsPanel.getCommentCharacter());
		value.setTransposeBeforeImport(dataSeriesImportOptionsPanel.isTransposeBeforeImport());
		value.setManualIndexData(dataSeriesImportOptionsPanel.isManualIndexData());
		value.setManualIndexValues(dataSeriesImportOptionsPanel.getManualIndexValues());
		value.setImportRowNames(dataSeriesImportOptionsPanel.isImportRowNames());
		
		return value;
	}

}
