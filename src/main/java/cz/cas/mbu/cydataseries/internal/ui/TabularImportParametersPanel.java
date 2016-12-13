package cz.cas.mbu.cydataseries.internal.ui;

import java.io.File;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;

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
import cz.cas.mbu.cydataseries.internal.dataimport.TabularFileImportParameters;
import cz.cas.mbu.cydataseries.internal.dataimport.DataSeriesImportParameters;
import cz.cas.mbu.cydataseries.internal.dataimport.FileFormatImportParameters;

import javax.swing.JButton;
import javax.swing.SwingConstants;

public class TabularImportParametersPanel extends JPanel {

	private String rawPreviewData;
	private boolean rawPreviewDataTruncated;
	private File inputFile;
	
	private FileImportOptionsPanel fileImportOptionsPanel;
	private RowColumnsImportOptionsPanel rowColumnsImportOptionsPanel;
	private ImportPreviewPanel importPreviewPanel;

	private final SelectColumnsToImportPanel columnsToImportPanel;
	
	private final Logger userLogger = Logger.getLogger(CyUserLog.NAME); 
	private JSeparator separator;
	private JButton btnSelectColumnsTo;
	
	private PreImportResults lastPreviewResults;
	private IndexImportOptionsPanel indexImportOptionsPanel;
	private JSeparator separator_1;
	private JSeparator separator_2;
	private JSeparator separator_3;
	
	/**
	 * Create the panel.
	 */
	public TabularImportParametersPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
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
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),}));
		
		fileImportOptionsPanel = new FileImportOptionsPanel();
		add(fileImportOptionsPanel, "2, 1, 1, 9, fill, fill");
		
		separator_2 = new JSeparator();
		separator_2.setOrientation(SwingConstants.VERTICAL);
		add(separator_2, "4, 1, 1, 10");
		
		rowColumnsImportOptionsPanel = new RowColumnsImportOptionsPanel();
		add(rowColumnsImportOptionsPanel, "6, 1, 5, 1, fill, fill");
		
		separator_1 = new JSeparator();
		add(separator_1, "6, 3, 5, 1");
		
		indexImportOptionsPanel = new IndexImportOptionsPanel();
		add(indexImportOptionsPanel, "6, 5, 5, 1, fill, fill");
		
		btnSelectColumnsTo = new JButton("Select columns to import...");
		btnSelectColumnsTo.addActionListener(evt -> showSelectColumnsToImport());
		
		separator_3 = new JSeparator();
		add(separator_3, "6, 7, 5, 1");
		add(btnSelectColumnsTo, "10, 9");
		
		separator = new JSeparator();
		add(separator, "2, 11, 9, 1");
		
		importPreviewPanel = new ImportPreviewPanel();
		add(importPreviewPanel, "2, 13, 9, 5, fill, fill");
		
		fileImportOptionsPanel.addChangedListener(evt -> updatePreview());
		rowColumnsImportOptionsPanel.addChangedListener(evt -> updatePreview());
		indexImportOptionsPanel.addChangedListener(evt -> updatePreview());
		
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
		String nameLower = inputFile.getName().toLowerCase();
		if(nameLower.endsWith(".tsv"))
		{
			fileImportOptionsPanel.suggestSeparator('\t');
		}
		else if (nameLower.endsWith(".csv"))
		{
			fileImportOptionsPanel.suggestSeparator(',');
		}
	}
	
	protected void updatePreview()
	{
		try {
			DataSeriesImportParameters dataSeriesImportParameters = getDataSeriesImportParameters();
			FileFormatImportParameters fileFormatImportParamaters = getFileFormatImportParamaters();
			lastPreviewResults = ImportHelper.preImport(new StringReader(rawPreviewData), fileFormatImportParamaters, dataSeriesImportParameters, false /*strict*/);
			importPreviewPanel.updatePreview(lastPreviewResults, dataSeriesImportParameters, fileFormatImportParamaters.isTransposeBeforeImport(), rawPreviewDataTruncated);
		}
		catch (Exception ex)
		{
			userLogger.error("Error creating import preview", ex);
			importPreviewPanel.showError(ex.getClass().getSimpleName() + ": " + ex.getMessage(), Arrays.asList(rawPreviewData.split("\n", 100)));			
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
			importPreviewPanel.showError("No import preview. Try clicking something else?", null);						
		}
	}
	
	public FileFormatImportParameters getFileFormatImportParamaters()
	{
		FileFormatImportParameters value = new FileFormatImportParameters();
		value.setSeparator(fileImportOptionsPanel.getSeparator());
		value.setCommentCharacter(fileImportOptionsPanel.getCommentCharacter());
		value.setTransposeBeforeImport(rowColumnsImportOptionsPanel.isTransposeBeforeImport());
		return value;
	}
	
	public DataSeriesImportParameters getDataSeriesImportParameters()
	{
		DataSeriesImportParameters value = new DataSeriesImportParameters();
		value.setIndexSource(indexImportOptionsPanel.getIndexSource());
		value.setManualIndexValues(indexImportOptionsPanel.getManualIndexValues());
		value.setImportRowNames(rowColumnsImportOptionsPanel.isImportRowNames());
		value.setImportAllColumns(columnsToImportPanel.isImportAllColumns());
		value.setImportedColumnIndices(columnsToImportPanel.getImportedColumnIndices());		
		
		return value;
	}

	public TabularFileImportParameters getTabularFileImportParameters()
	{
		TabularFileImportParameters value = new TabularFileImportParameters(getFileFormatImportParamaters(), getDataSeriesImportParameters());
		value.setFile(inputFile);
		return value;
	}
}
