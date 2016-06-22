package cz.cas.mbu.cytimeseries.internal.ui;

import javax.swing.JPanel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JButton;
import javax.swing.JTable;

public class MappingManagerPanel extends JPanel {
	private JTable table;

	/**
	 * Create the panel.
	 */
	public MappingManagerPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		table = new JTable();
		add(table, "2, 2, 3, 1, fill, fill");
		
		JButton btnAddMapping = new JButton("Add");
		add(btnAddMapping, "2, 4");
		
		JButton btnRemoveMapping = new JButton("Remove Selected");
		add(btnRemoveMapping, "4, 4");
		
	}

}
