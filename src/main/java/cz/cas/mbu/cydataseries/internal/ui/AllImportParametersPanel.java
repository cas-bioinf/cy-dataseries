package cz.cas.mbu.cydataseries.internal.ui;

import java.io.File;
import java.io.StringReader;

import javax.swing.JOptionPane;
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
import javax.swing.JButton;

public class AllImportParametersPanel extends JPanel {

	private String rawPreviewData;
	private boolean rawPreviewDataTruncated;
	private File inputFile;
	
	private FileImportOptionsPanel fileImportOptionsPanel;
	private DataSeriesImportOptionsPanel dataSeriesImportOptionsPanel;
	private ImportPreviewPanel importPreviewPanel;

	private final SelectColumnsToImportPanel columnsToImportPanel;
	
	private final Logger userLogger = Logger.getLogger(CyUserLog.NAME); 
	private JSeparator separator;
	private JButton btnSelectColumnsTo;
	
	private PreImportResults lastPreviewResults;
	
	/**
	 * Create the panel.
	 */
	public AllImportParametersPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
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
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),}));
		
		fileImportOptionsPanel = new FileImportOptionsPanel();
		add(fileImportOptionsPanel, "2, 1, 1, 3, fill, fill");
		
		dataSeriesImportOptionsPanel = new DataSeriesImportOptionsPanel();
		add(dataSeriesImportOptionsPanel, "4, 1, 5, 1, fill, fill");
		
		btnSelectColumnsTo = new JButton("Select columns to import...");
		btnSelectColumnsTo.addActionListener(evt -> showSelectColumnsToImport());
		add(btnSelectColumnsTo, "8, 3");
		
		separator = new JSeparator();
		add(separator, "2, 5, 7, 1");
		
		importPreviewPanel = new ImportPreviewPanel();
		add(importPreviewPanel, "2, 7, 7, 1, fill, fill");
		
		fileImportOptionsPanel.addChangedListener(evt -> updatePreview());
		dataSeriesImportOptionsPanel.addChangedListener(evt -> updatePreview());
		
		columnsToImportPanel = new SelectColumnsToImportPanel();
	}
	
	public void setPreviewData(String rawData, boolean rawDataTruncated)
	{
		rawPreviewData = rawData;
		this.rawPreviewDataTruncated = rawDataTruncated;
		updatePreview();
	}
	
	public void setInputfile(File inputFile)
	{
		this.inputFile = inputFile;
	}
	
	protected void updatePreview()
	{
		try {
			lastPreviewResults = ImportHelper.preImport(new StringReader(rawPreviewData), getImportParameters(), false /*strict*/);
			importPreviewPanel.updatePreview(lastPreviewResults, getImportParameters(), rawPreviewDataTruncated);
		}
		catch (Exception ex)
		{
			userLogger.error("Error creating import preview", ex);
			importPreviewPanel.showError(ex.getClass().getSimpleName() + ": " + ex.getMessage());			
		}
	}
	
	protected void showSelectColumnsToImport()
	{
		if(lastPreviewResults != null)
		{
			columnsToImportPanel.setAvailableColumns(lastPreviewResults.getOriginalIndexValues(), null);
			JOptionPane.showMessageDialog(this, columnsToImportPanel, "Select columns to import", JOptionPane.PLAIN_MESSAGE);
			updatePreview();
		}
		else {
			importPreviewPanel.showError("No import preview. Try clicking something else?");						
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
		value.setIndexSource(dataSeriesImportOptionsPanel.getIndexSource());
		value.setManualIndexValues(dataSeriesImportOptionsPanel.getManualIndexValues());
		value.setImportRowNames(dataSeriesImportOptionsPanel.isImportRowNames());
		value.setImportAllColumns(columnsToImportPanel.isImportAllColumns());
		value.setImportedColumnIndices(columnsToImportPanel.getImportedColumnIndices());		
		
		return value;
	}

}
