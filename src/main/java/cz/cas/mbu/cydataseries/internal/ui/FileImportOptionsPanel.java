package cz.cas.mbu.cydataseries.internal.ui;

import javax.swing.JPanel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JSeparator;
import javax.swing.ButtonGroup;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.ChangedCharSetException;
import javax.swing.event.CaretEvent;
import javax.swing.event.ChangeEvent;

public class FileImportOptionsPanel extends JPanel {
	
	private List<ChangeListener> parametersChangedListeners;
	
	private JTextField otherSeparatorTextField;
	private JTextField commentTextField;
	private final ButtonGroup separatorButtonGroup = new ButtonGroup();
	private JRadioButton rdbtnTab;
	private JRadioButton rdbtnSpace;
	private JRadioButton rdbtnComma;
	private JRadioButton rdbtnSemicolon;
	private JRadioButton rdbtnOther;
	private JLabel lblOneCharOnly;
	private JLabel lblOneCharOnly_1;

	/**
	 * Create the panel.
	 */
	public FileImportOptionsPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
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
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		JLabel lblNewLabel = new JLabel("Delimiter:");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		add(lblNewLabel, "2, 2");
		
		rdbtnComma = new JRadioButton(", (Comma)");
		rdbtnComma.setSelected(true);
		separatorButtonGroup.add(rdbtnComma);
		add(rdbtnComma, "4, 2");
		
		rdbtnSemicolon = new JRadioButton("; (Semicolon)");
		separatorButtonGroup.add(rdbtnSemicolon);
		add(rdbtnSemicolon, "4, 4");
		
		rdbtnSpace = new JRadioButton("SPACE");
		separatorButtonGroup.add(rdbtnSpace);
		add(rdbtnSpace, "4, 6");
		
		rdbtnTab = new JRadioButton("TAB");
		separatorButtonGroup.add(rdbtnTab);
		add(rdbtnTab, "4, 8");
		
		rdbtnOther = new JRadioButton("Other");
		rdbtnOther.addItemListener(eventIgnored ->
		{
			otherSeparatorTextField.setEnabled(rdbtnOther.isSelected());
		});
		separatorButtonGroup.add(rdbtnOther);
		add(rdbtnOther, "4, 10");
		
		otherSeparatorTextField = new JTextField();
		otherSeparatorTextField.setEnabled(false);
		add(otherSeparatorTextField, "6, 10, left, default");
		otherSeparatorTextField.setColumns(2);
		
		lblOneCharOnly_1 = new JLabel("One char only");
		lblOneCharOnly_1.setFont(new Font("Tahoma", Font.ITALIC, 11));
		add(lblOneCharOnly_1, "8, 10");
		
		JSeparator separator = new JSeparator();
		add(separator, "2, 12, 7, 1");
		
		JLabel lblIgnoreLinesStarting = new JLabel("Ignore lines starting with:");
		lblIgnoreLinesStarting.setHorizontalAlignment(SwingConstants.LEFT);
		lblIgnoreLinesStarting.setFont(new Font("Tahoma", Font.BOLD, 11));
		add(lblIgnoreLinesStarting, "2, 14, 3, 1, left, default");
		
		commentTextField = new JTextField();
		add(commentTextField, "6, 14, left, default");
		commentTextField.setColumns(2);
		
		lblOneCharOnly = new JLabel("One char only");
		lblOneCharOnly.setFont(new Font("Tahoma", Font.ITALIC, 11));
		add(lblOneCharOnly, "8, 14");
		
		parametersChangedListeners = new ArrayList<>();
		rdbtnComma.addItemListener(this::radioButtonChanged);
		rdbtnSemicolon.addItemListener(this::radioButtonChanged);
		rdbtnSpace.addItemListener(this::radioButtonChanged);
		rdbtnTab.addItemListener(this::radioButtonChanged);
		rdbtnOther.addItemListener(this::radioButtonChanged);
		
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
		commentTextField.getDocument().addDocumentListener(documentchangeListener);
		otherSeparatorTextField.getDocument().addDocumentListener(documentchangeListener);
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
	
	public char getSeparator()
	{
		if(getRdbtnComma().isSelected())
		{
			return ',';
		}
		else if(getRdbtnSemicolon().isSelected())
		{
			return ';';
		}
		else if(getRdbtnSpace().isSelected())
		{
			return ' ';
		}
		else if(getRdbtnTab().isSelected())
		{
			return '\t';
		}
		else if(getRdbtnOther().isSelected())
		{
			if(getOtherSeparatorTextField().getText().isEmpty())
			{
				return ',';
			}
			else
			{
				return getOtherSeparatorTextField().getText().charAt(0);
			}
		}
		else 
		{
			return ',';
		}
	}
	
	public Character getCommentCharacter()
	{
		if(getCommentTextField().getText().isEmpty())
		{
			return null;
		}
		else 
		{
			return getCommentTextField().getText().charAt(0);
		}
	}
	
	protected JRadioButton getRdbtnTab() {
		return rdbtnTab;
	}
	protected JRadioButton getRdbtnSpace() {
		return rdbtnSpace;
	}
	protected JRadioButton getRdbtnComma() {
		return rdbtnComma;
	}
	protected JRadioButton getRdbtnSemicolon() {
		return rdbtnSemicolon;
	}
	protected JRadioButton getRdbtnOther() {
		return rdbtnOther;
	}
	protected JTextField getOtherSeparatorTextField() {
		return otherSeparatorTextField;
	}
	protected JTextField getCommentTextField() {
		return commentTextField;
	}
}
