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

public class IndexImportOptionsPanel extends JPanel implements TunableValidator{
	private List<ChangeListener> parametersChangedListeners;
	
	private JTextField textFieldIndexValues;
	private final ButtonGroup buttonGroupIndexSource = new ButtonGroup();
	private JRadioButton rdbtnIndexFromHeader;
	private JRadioButton rdbtnManualIndexValuesAdd;
	private JRadioButton rdbtnManualIndexValuesOverride;
	private JLabel lblManualIndex;

	
	private final DocumentListener documentchangeListener = new DocumentListener() {

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

	/**
	 * Create the panel.
	 */
	public IndexImportOptionsPanel() {
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
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		rdbtnIndexFromHeader = new JRadioButton("Index values from header");
		rdbtnIndexFromHeader.setSelected(true);
		rdbtnIndexFromHeader.addItemListener(e -> indexSourceChanged());
		buttonGroupIndexSource.add(rdbtnIndexFromHeader);
		add(rdbtnIndexFromHeader, "2, 2");
		rdbtnIndexFromHeader.addItemListener(this::radioButtonChanged);
		
		rdbtnManualIndexValuesAdd = new JRadioButton("Manual index values (add)");
		rdbtnManualIndexValuesAdd.addItemListener(e -> indexSourceChanged());
		
				buttonGroupIndexSource.add(rdbtnManualIndexValuesAdd);
				add(rdbtnManualIndexValuesAdd, "4, 2");
				rdbtnManualIndexValuesAdd.addItemListener(this::radioButtonChanged);
		
		rdbtnManualIndexValuesOverride = new JRadioButton("Manual index values (override)");
		rdbtnManualIndexValuesOverride.addItemListener(e -> indexSourceChanged());
		buttonGroupIndexSource.add(rdbtnManualIndexValuesOverride);
		add(rdbtnManualIndexValuesOverride, "6, 2");
		
		lblManualIndex = new JLabel("Manual index:");
		add(lblManualIndex, "2, 4, right, default");
		
		textFieldIndexValues = new JTextField();
		textFieldIndexValues.setEnabled(false);
		add(textFieldIndexValues, "4, 4, 3, 1, fill, default");
		textFieldIndexValues.setColumns(10);
		textFieldIndexValues.getDocument().addDocumentListener(documentchangeListener);
		
		JLabel lblCommaSeparatedSupports = new JLabel("<html>Comma separated, supports Matlab notation for numbers<br>\r\n(e.g.,\"1:3,4:0.5:6\" &lt;-&gt; \"1,2,3,4,4.5,5,5.5,6\")</html>");
		lblCommaSeparatedSupports.setFont(new Font("Tahoma", Font.ITALIC, 11));
		add(lblCommaSeparatedSupports, "4, 6, 3, 1");

		parametersChangedListeners = new ArrayList<>();
				
		indexSourceChanged();
	}
	
	public void setManualAddVisible(boolean visible)
	{
		rdbtnManualIndexValuesAdd.setVisible(visible);
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
		
	public DataSeriesImportParameters.IndexSource getIndexSource()
	{
		if (rdbtnIndexFromHeader.isSelected()) {
			return DataSeriesImportParameters.IndexSource.Data;
		}
		else if (rdbtnManualIndexValuesAdd.isSelected())
		{
			return DataSeriesImportParameters.IndexSource.ManualAdd;
		}
		else 
		{
			return DataSeriesImportParameters.IndexSource.ManualOverride;
		}
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
		catch(NumberFormatException ex) {
			return Collections.EMPTY_LIST;
		}
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
	
	protected void indexSourceChanged()
	{
		boolean manualEnabled = getIndexSource() != DataSeriesImportParameters.IndexSource.Data;
		Component [] manualComponents = new Component[] {textFieldIndexValues, lblManualIndex};
		for(Component c: manualComponents)
		{
			c.setEnabled(manualEnabled);
		}
	}

	protected JRadioButton getRdbtnIndexFromHeader() {
		return rdbtnIndexFromHeader;
	}
	protected JTextField getTextFieldIndexValues() {
		return textFieldIndexValues;
	}

}
