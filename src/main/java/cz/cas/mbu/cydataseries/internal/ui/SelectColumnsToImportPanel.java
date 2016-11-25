package cz.cas.mbu.cydataseries.internal.ui;

import javax.swing.JPanel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JList;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JScrollPane;

public class SelectColumnsToImportPanel extends JPanel {

	private final JCheckBox chckbxImportAllColumns;
	
	private final CheckBoxList columnList;

	private final JButton btnCheckAll;
	private final JButton btnUncheckAll;
	private final JScrollPane scrollPane;
	private final JLabel lblSelectColumnsTo; 
	/**
	 * Create the panel.
	 */
	public SelectColumnsToImportPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		chckbxImportAllColumns = new JCheckBox("Import All Columns");
		chckbxImportAllColumns.setSelected(true);
		chckbxImportAllColumns.addItemListener(evt -> updateColumnSelectionEnable());
		add(chckbxImportAllColumns, "2, 2, 3, 1");
		
		JSeparator separator = new JSeparator();
		add(separator, "2, 4, 3, 1");
		
		lblSelectColumnsTo = new JLabel("Select columns to import (the table refers to columns in the raw data):");
		lblSelectColumnsTo.setEnabled(false);
		add(lblSelectColumnsTo, "2, 6, 3, 1");
		
		btnCheckAll = new JButton("Check all");
		btnCheckAll.setEnabled(false);
		btnCheckAll.addActionListener(evt -> setAll(true));
		add(btnCheckAll, "2, 8");
		
		btnUncheckAll = new JButton("Uncheck all");
		btnUncheckAll.setEnabled(false);
		btnUncheckAll.addActionListener(evt -> setAll(false));
		add(btnUncheckAll, "4, 8");
		
		scrollPane = new JScrollPane();
		scrollPane.setEnabled(false);
		add(scrollPane, "2, 10, 3, 1, fill, fill");
		
		columnList = new CheckBoxList();
		scrollPane.setViewportView(columnList);

	}
	
	private void setAll(boolean selected)
	{
		for(int i = 0; i < columnList.getModel().getSize(); i++)
		{
			columnList.getModel().getElementAt(i).setSelected(selected);
		}
		columnList.invalidate();
		columnList.repaint();
	}
	
	private void updateColumnSelectionEnable()
	{
		Component[] affectedComponents = new Component[] { columnList, scrollPane, btnCheckAll, btnUncheckAll, lblSelectColumnsTo };
		for(Component c: affectedComponents)
		{
			c.setEnabled(!chckbxImportAllColumns.isSelected());
		}
	}
	
	public boolean isImportAllColumns(){
		return chckbxImportAllColumns.isSelected();
	}
	
	public List<Integer> getImportedColumnIndices(){
		List<Integer> result = new ArrayList<>();
		ListModel<CheckBoxList.Item> model = columnList.getModel();
		for(int i = 0; i < model.getSize(); i++)
		{
			if(model.getElementAt(i).isSelected())
			{
				result.add(i);
			}
		}
		return result;
	}
	
	public void setAvailableColumns(List<String> columnNames, List<String> columnDescriptions)
	{
		if(columnDescriptions != null && columnNames.size() != columnDescriptions.size())
		{
			throw new IllegalArgumentException("Column names and column descriptions must have the same name");
		}
		
		List<Integer> previouslySelected = getImportedColumnIndices();
		
		DefaultListModel<CheckBoxList.Item> newModel = new DefaultListModel<>();
		for(int i = 0; i < columnNames.size(); i++)
		{
			String label;
			if(columnDescriptions != null)
			{
				label = Integer.toString(i) + ": \"" + columnNames.get(i) + "\" (" + columnDescriptions + ")";
			}
			else
			{
				label = Integer.toString(i) + ": \"" + columnNames.get(i) + "\"";
			}
			newModel.addElement(new CheckBoxList.Item(label));
		}
		
		previouslySelected.forEach(column -> {
			if(column < newModel.size())
			{
				newModel.get(column).setSelected(true);
			}
		});
		
		columnList.setModel(newModel);
	}
}
