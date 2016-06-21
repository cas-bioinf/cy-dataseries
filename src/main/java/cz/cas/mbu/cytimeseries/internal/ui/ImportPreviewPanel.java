package cz.cas.mbu.cytimeseries.internal.ui;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import cz.cas.mbu.cytimeseries.dataimport.PreImportResults;

import javax.swing.JScrollPane;

public class ImportPreviewPanel extends JPanel {
	private JTable table;

	/**
	 * Create the panel.
	 */
	public ImportPreviewPanel() {
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane);
		
		table = new JTable();
		scrollPane.setViewportView(table);

	}

	public void updatePreview(PreImportResults preImportResults)
	{
		int numRows = Math.max(preImportResults.getRowNames().size(), preImportResults.getCellData().length);
		int numColumns = Math.max(preImportResults.getIndexValues().size(), preImportResults.getCellData().length > 0 ? preImportResults.getCellData()[0].length : 0) + 1;
		Object[][] modelData = new Object[numRows][numColumns];
		
		for(int row = 0; row < preImportResults.getCellData().length; row++)
		{
			if(row < preImportResults.getRowNames().size())
			{
				modelData[row][0] = preImportResults.getRowNames().get(row);
			}
			for(int column = 0; column < preImportResults.getCellData()[row].length; column++)
			{
				modelData[row][column + 1] = preImportResults.getCellData()[row][column];
			}
		}
		
		Object[] columnNames = new Object[numColumns];
		columnNames[0] = "Row Name";
		for(int column = 0; column < preImportResults.getIndexValues().size(); column++)
		{
			columnNames[column + 1] = preImportResults.getIndexValues().get(column);
		}
		DefaultTableModel model = new DefaultTableModel(modelData, columnNames);
		table.setModel(model);
	}
	
	public void showError(String error)
	{
		Object[] columns = new Object[] { "There was an error in generating preview" };
		Object[][] data = new Object[][] { { error } };
		table.setModel(new DefaultTableModel(data, columns));
	}
}
