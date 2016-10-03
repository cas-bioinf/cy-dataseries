package cz.cas.mbu.cydataseries.internal.ui;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.cytoscape.application.swing.CytoPanelComponent2;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.service.util.CyServiceRegistrar;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import cz.cas.mbu.cydataseries.DataSeries;
import cz.cas.mbu.cydataseries.DataSeriesEvent;
import cz.cas.mbu.cydataseries.DataSeriesListener;
import cz.cas.mbu.cydataseries.DataSeriesManager;
import cz.cas.mbu.cydataseries.DataSeriesStorageManager;
import cz.cas.mbu.cydataseries.DataSeriesStorageProvider;

public class DataSeriesPanel extends JPanel implements CytoPanelComponent2, DataSeriesListener {
	private JTable table;
	private JComboBox<DataSeries<?, ?>> dataSeriesComboBox;
	
	private DataSeries<?, ?> selectedDataSeries;
	
	private final CyServiceRegistrar registrar;
	private JLabel lblTypeCaption;
	private JLabel lblDSType;
	
	/** Create panel. */
	public DataSeriesPanel(CyServiceRegistrar registrar) {
		this.registrar = registrar;
		
		setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.UNRELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),}));
		
		JLabel lblDataSeries = new JLabel("Data Series:");
		add(lblDataSeries, "2, 2, right, default");
		
		dataSeriesComboBox = new JComboBox<DataSeries<?, ?>>();
		dataSeriesComboBox.addItemListener( e -> {
			if(e.getStateChange() == ItemEvent.SELECTED)
			{
				updateSelectedDataSeries();
			}
		});
		add(dataSeriesComboBox, "4, 2, fill, default");
		
		lblTypeCaption = new JLabel("Type:");
		add(lblTypeCaption, "6, 2");
		
		lblDSType = new JLabel("New label");
		add(lblDSType, "8, 2");
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, "2, 4, 7, 1, fill, fill");
		
		table = new JTable();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scrollPane.setViewportView(table);

		updateDataSeriesComboBox();
	}

	
	
	@Override
	public void dataSeriesEvent(DataSeriesEvent event) {
		updateDataSeriesComboBox();
	}



	public final void updateDataSeriesComboBox()
	{
		DataSeriesManager manager = registrar.getService(DataSeriesManager.class);
		List<DataSeries<?,?>> ds = manager.getAllDataSeries();
		DataSeries[] dsArray = new DataSeries[ds.size()];
		dataSeriesComboBox.setModel(new DefaultComboBoxModel<DataSeries<?, ?>>(ds.toArray(dsArray)));
		if(selectedDataSeries != null && ds.contains(selectedDataSeries))
		{
			dataSeriesComboBox.setSelectedItem(selectedDataSeries);
		}
		else
		{
			selectedDataSeries = null;
			updateSelectedDataSeries();
		}
	}
	
	protected void updateSelectedDataSeries()
	{
		if(dataSeriesComboBox.getSelectedItem() == null || !(dataSeriesComboBox.getSelectedItem() instanceof DataSeries))
		{
			selectedDataSeries = null;
			table.setModel(new DefaultTableModel(new Object[][] { { "No data series selected"}}, new Object[] {""}));
			lblDSType.setText("N/A");			
		}
		else
		{
			selectedDataSeries = (DataSeries<?,?>)dataSeriesComboBox.getSelectedItem();
			
			DataSeriesStorageProvider storageProvider = registrar.getService(DataSeriesStorageManager.class).getStorageProvider(selectedDataSeries.getClass());
			if(storageProvider == null)
			{
				lblDSType.setText("N/A");
			}
			else
			{
				lblDSType.setText(storageProvider.getSeriesTypeCaption());				
			}
			
			Object[] columnNames = new Object[selectedDataSeries.getIndexCount() + 2]; //+2 for index and row names
			columnNames[0] = "Id";
			columnNames[1] = "Row name";
			for(int col = 0; col < selectedDataSeries.getIndexCount(); col++)
			{
				columnNames[col + 2] = selectedDataSeries.getIndex().get(col);
			}
			
			Object[][] data = new Object[selectedDataSeries.getRowCount()][selectedDataSeries.getIndexCount() + 2];
			for(int row = 0; row < selectedDataSeries.getRowCount(); row++)
			{
				data[row][0] = selectedDataSeries.getRowID(row);
				data[row][1] = selectedDataSeries.getRowName(row);
				for (int col = 0; col < selectedDataSeries.getIndexCount(); col++)
				{
					data[row][col + 2] = selectedDataSeries.getRowData(row).get(col);
				}
			}
			DefaultTableModel model = new DefaultTableModel(data, columnNames);
			table.setModel(model);
			for(int col = 0; col < table.getColumnModel().getColumnCount(); col++)
			{
				table.getColumnModel().getColumn(col).setMinWidth(50);
			}
			
		}
	}
	
	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.SOUTH;
	}

	@Override
	public String getTitle() {
		return "Data Series";
	}

	@Override
	public Icon getIcon() {
		return null;
	}

	@Override
	public String getIdentifier() {
		return "cz.cas.mbu.cydataseries.dataSeries";
	}
	
	protected JComboBox getDataSeriesComboBox() {
		return dataSeriesComboBox;
	}
}
