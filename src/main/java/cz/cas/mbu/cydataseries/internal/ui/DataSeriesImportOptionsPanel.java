package cz.cas.mbu.cydataseries.internal.ui;

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

import cz.cas.mbu.cydataeseries.internal.MatlabSyntaxNumberList;

import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ChangeEvent;

public class DataSeriesImportOptionsPanel extends JPanel implements TunableValidator{
	private List<ChangeListener> parametersChangedListeners;
	
	private JTextField textFieldIndexValues;
	private final ButtonGroup buttonGroupTranspose = new ButtonGroup();
	private final ButtonGroup buttonGroupIndexSource = new ButtonGroup();
	private JRadioButton rdbtnIndexFromHeader;
	private JRadioButton rdbtnColumnsAsIndex;
	private JRadioButton rdbtnManualIndexValues;
	private JCheckBox chckbxDataContainesNames;
	private JRadioButton rdbtnRowsAsIndex;

	/**
	 * Create the panel.
	 */
	public DataSeriesImportOptionsPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
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
				FormSpecs.DEFAULT_ROWSPEC,
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
		add(rdbtnRowsAsIndex, "4, 2");
		
		JSeparator separator = new JSeparator();
		add(separator, "2, 4, 3, 1");
		
		rdbtnIndexFromHeader = new JRadioButton("Index values from header");
		rdbtnIndexFromHeader.setSelected(true);
		buttonGroupIndexSource.add(rdbtnIndexFromHeader);
		add(rdbtnIndexFromHeader, "2, 6");
		
		rdbtnManualIndexValues = new JRadioButton("Manual index values");
		rdbtnManualIndexValues.addItemListener(e -> {
			textFieldIndexValues.setEnabled(rdbtnManualIndexValues.isSelected());
		});
		buttonGroupIndexSource.add(rdbtnManualIndexValues);
		add(rdbtnManualIndexValues, "4, 6");
		
		textFieldIndexValues = new JTextField();
		textFieldIndexValues.setEnabled(false);
		add(textFieldIndexValues, "4, 8, fill, default");
		textFieldIndexValues.setColumns(10);
		
		JLabel lblCommaSeparatedSupports = new JLabel("<html>Comma separated, supports Matlab notation for numbers<br>\r\n(e.g.,\"1:3,4:0.5:6\" &lt;-&gt; \"1,2,3,4,4.5,5,5.5,6\")</html>");
		lblCommaSeparatedSupports.setFont(new Font("Tahoma", Font.ITALIC, 11));
		add(lblCommaSeparatedSupports, "4, 10");
		
		JSeparator separator_1 = new JSeparator();
		add(separator_1, "2, 12, 3, 1");
		
		chckbxDataContainesNames = new JCheckBox("Data start with names for dependent variables");
		chckbxDataContainesNames.setSelected(true);
		add(chckbxDataContainesNames, "2, 14, 3, 1");

		parametersChangedListeners = new ArrayList<>();
		
		rdbtnColumnsAsIndex.addItemListener(this::radioButtonChanged);
		rdbtnRowsAsIndex.addItemListener(this::radioButtonChanged);
		rdbtnIndexFromHeader.addItemListener(this::radioButtonChanged);
		rdbtnManualIndexValues.addItemListener(this::radioButtonChanged);
		
		chckbxDataContainesNames.addItemListener(evt -> fireChangeEvent());
		
		
		DocumentListener documentchangeListener = new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				fireChangeEvent();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				fireChangeEvent();				
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				fireChangeEvent();
			}
			
		};
		textFieldIndexValues.getDocument().addDocumentListener(documentchangeListener);		
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
		
	protected void radioButtonChanged(ItemEvent evt)
	{
		if(evt.getStateChange() == ItemEvent.SELECTED)
		{
			fireChangeEvent();
		}
	}
	
	
	public boolean isTransposeBeforeImport()
	{
		return getRdbtnRowsAsIndex().isSelected();
	}
	
	public boolean isManualIndexData()
	{
		return getRdbtnManualIndexValues().isSelected();
	}
	
	
	/**
	 * May throw exceptions - used for both returning a value and validating.
	 * @return
	 */
	private List<String> getManualIndexValuesInternal()
	{
		String value = getTextFieldIndexValues().getText(); 
		if(value.matches("^[0-9,:]*$"))
		{
			return MatlabSyntaxNumberList.listFromString(value).stream()
					.map(x -> Double.toString(x))
					.collect(Collectors.toList());
		}
		else
		{
			return Arrays.asList(value.split(","));
		}		
	}
	
	public List<String> getManualIndexValues()
	{
		try {
			return getManualIndexValuesInternal();
		}
		catch(NumberFormatException ex)
		{
			return Collections.EMPTY_LIST;
		}
	}

	public boolean isImportRowNames()
	{
		return getChckbxDataContainesNames().isSelected();
	}
	
	
	@Override
	public ValidationState getValidationState(Appendable errMsg) {
		try {
			try {
				getManualIndexValuesInternal();
			}
			catch(NumberFormatException ex)
			{
				errMsg.append(ex.getMessage());
				return ValidationState.INVALID;
			}
			return ValidationState.OK;
		}
		catch(IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	protected JRadioButton getRdbtnIndexFromHeader() {
		return rdbtnIndexFromHeader;
	}
	protected JRadioButton getRdbtnColumnsAsIndex() {
		return rdbtnColumnsAsIndex;
	}
	protected JRadioButton getRdbtnManualIndexValues() {
		return rdbtnManualIndexValues;
	}
	protected JTextField getTextFieldIndexValues() {
		return textFieldIndexValues;
	}
	protected JCheckBox getChckbxDataContainesNames() {
		return chckbxDataContainesNames;
	}
	protected JRadioButton getRdbtnRowsAsIndex() {
		return rdbtnRowsAsIndex;
	}
}
