package cz.cas.mbu.cydataseries.internal.ui;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import cz.cas.mbu.cydataseries.dataimport.PreImportResults;
import cz.cas.mbu.cydataseries.internal.dataimport.ImportParameters;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import java.awt.Font;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.SwingConstants;

public class ImportPreviewPanel extends JPanel {
	private JTable table;
	private JLabel lblDimensions; 
	
	public static final int MAX_ROWS_TO_DISPLAY = 200;
	public static final int MAX_COLS_TO_DISPLAY = 200;
	
	/**
	 * Create the panel.
	 */
	public ImportPreviewPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.DEFAULT_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				RowSpec.decode("14px"),
				RowSpec.decode("286px"),}));
		
		JLabel lblNewLabel = new JLabel("Preview (first 100 lines of the file):");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		add(lblNewLabel, "1, 1, fill, top");
		
		lblDimensions = new JLabel("100+ data points for 30 dependent variables");
		lblDimensions.setHorizontalAlignment(SwingConstants.TRAILING);
		add(lblDimensions, "2, 1");

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		add(scrollPane, "1, 2, 2, 1, fill, fill");

		table = new JTable();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scrollPane.setViewportView(table);

	}

	public void updatePreview(PreImportResults preImportResults, ImportParameters params, boolean rawDataTruncated) {
		int numRows = Math.max(preImportResults.getRowNames().size(), preImportResults.getCellData().length);
		numRows = Math.min(numRows, MAX_ROWS_TO_DISPLAY);
		
		int numColumns = Math.max(preImportResults.getIndexValues().size(),
				preImportResults.getCellData().length > 0 ? preImportResults.getCellData()[0].length : 0) + 1;
		numColumns = Math.min(numColumns, MAX_COLS_TO_DISPLAY + 1);
		
		Object[][] modelData = new Object[numRows][numColumns];

		for (int row = 0; row < Math.min(preImportResults.getCellData().length, MAX_ROWS_TO_DISPLAY); row++) {
			if (row < preImportResults.getRowNames().size()) {
				modelData[row][0] = preImportResults.getRowNames().get(row);
			}
			for (int column = 0; column < Math.min(preImportResults.getCellData()[row].length, MAX_COLS_TO_DISPLAY); column++) {
				modelData[row][column + 1] = preImportResults.getCellData()[row][column];
			}
		}

		Object[] columnNames = new Object[numColumns];
		columnNames[0] = "Row Name";
		for (int column = 0; column < Math.min(preImportResults.getIndexValues().size(), MAX_COLS_TO_DISPLAY); column++) {
			columnNames[column + 1] = preImportResults.getIndexValues().get(column);
		}
		DefaultTableModel model = new DefaultTableModel(modelData, columnNames);
		table.setModel(model);
		for(int col = 0; col < table.getColumnModel().getColumnCount(); col++)
		{
			table.getColumnModel().getColumn(col).setMinWidth(50);
		}
		
		StringBuilder dimensionsText = new StringBuilder();
	    dimensionsText.append(preImportResults.getIndexValues().size());
	    if(rawDataTruncated && params.isTransposeBeforeImport() && params.getIndexSource() == ImportParameters.IndexSource.Data)
	    {
	    	dimensionsText.append("+");
	    }
	    dimensionsText.append(" data points for " );
	    
	    dimensionsText.append(preImportResults.getRowNames().size());
	    if(rawDataTruncated && !params.isTransposeBeforeImport())
	    {
	    	dimensionsText.append("+");
	    }
	    dimensionsText.append(" dependent variables");
	    
	    lblDimensions.setText(dimensionsText.toString());
	}

	public void showError(String error) {
		Object[] columns = new Object[] { "There was an error in generating preview" };
		Object[][] data = new Object[][] { { error } };
		table.setModel(new DefaultTableModel(data, columns));
	}
}
