package cz.cas.mbu.cydataseries.internal.ui;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.TunableValidator.ValidationState;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import cz.cas.mbu.cydataseries.internal.dataimport.DataSeriesImportParameters;
import cz.cas.mbu.cydataseries.internal.dataimport.MatlabSyntaxNumberList;

import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ChangeEvent;

/**
 * Data series import dialog.
 *
 * Data can be imported as CSV. The dialog allows for selection of separators,
 *
 */

public class RowColumnsImportOptionsPanel extends JPanel implements TunableValidator{
	private List<ChangeListener> parametersChangedListeners;
	private final ButtonGroup buttonGroupTranspose = new ButtonGroup();
	private JRadioButton rdbtnColumnsAsIndex;
	private JCheckBox chckbxDataContainesNames;
	private JRadioButton rdbtnRowsAsIndex;

	/**
	 * Create the panel.
	 */
	public RowColumnsImportOptionsPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		rdbtnColumnsAsIndex = new JRadioButton("Columns as indices");
		rdbtnColumnsAsIndex.setSelected(true);
		buttonGroupTranspose.add(rdbtnColumnsAsIndex);
		add(rdbtnColumnsAsIndex, "2, 2");
		
		rdbtnRowsAsIndex = new JRadioButton("Rows as indices (transpose)");
		rdbtnRowsAsIndex.setSelected(true);
		buttonGroupTranspose.add(rdbtnRowsAsIndex);
		add(rdbtnRowsAsIndex, "4, 2, 3, 1");
		
		chckbxDataContainesNames = new JCheckBox("Data start with names for dependent variables");
		chckbxDataContainesNames.setSelected(true);
		add(chckbxDataContainesNames, "2, 4");
		
		chckbxDataContainesNames.addItemListener(evt -> fireChangeEvent());

		parametersChangedListeners = new ArrayList<>();
		
		rdbtnColumnsAsIndex.addItemListener(this::radioButtonChanged);
		rdbtnRowsAsIndex.addItemListener(this::radioButtonChanged);				
	}
	
	public void addChangedListener(ChangeListener x)
	{
		parametersChangedListeners.add(x);
	}
	
	protected void fireChangeEvent()
	{
		ChangeEvent evt = new ChangeEvent(this);
		parametersChangedListeners.forEach(listener -> listener.stateChanged(evt));
	}
		
	protected void radioButtonChanged(ItemEvent evt) {
		if(evt.getStateChange() == ItemEvent.SELECTED)
		{
			fireChangeEvent();
		}
	}
	
	
	public boolean isTransposeBeforeImport()
	{
		return getRdbtnRowsAsIndex().isSelected();
	}			
	
	public boolean isImportRowNames()
	{
		return getChckbxDataContainesNames().isSelected();
	}
	
	
	@Override
	public ValidationState getValidationState(Appendable errMsg) {
		return ValidationState.OK;
	}
	
	protected JRadioButton getRdbtnColumnsAsIndex() {
		return rdbtnColumnsAsIndex;
	}
	protected JCheckBox getChckbxDataContainesNames() {
		return chckbxDataContainesNames;
	}
	protected JRadioButton getRdbtnRowsAsIndex() {
		return rdbtnRowsAsIndex;
	}

}
