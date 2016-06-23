package cz.cas.mbu.cydataseries.internal.ui;

import javax.swing.JPanel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import cz.cas.mbu.cydataeseries.internal.MapColumnTask;
import cz.cas.mbu.cydataeseries.internal.RemoveColumnMappingTask;
import cz.cas.mbu.cydataseries.DataSeriesManager;
import cz.cas.mbu.cydataseries.DataSeriesMappingManager;
import cz.cas.mbu.cydataseries.DataSeriesMappingManager.MappingDescriptor;

import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;

import java.awt.Dimension;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class MappingManagerPanel extends JPanel {
	private JTable table;

	CyServiceRegistrar registrar;
	
	List<MappingDescriptor> displayedDescriptors;
	
	/**
	 * Create the panel.
	 */
	public MappingManagerPanel(CyServiceRegistrar registrar) {
		this.registrar = registrar;
		setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, "2, 2, 3, 1, fill, fill");
		
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(table);
		table.setMinimumSize(new Dimension(100, 100));
		
		JButton btnAddMapping = new JButton("Add");
		btnAddMapping.addActionListener(e -> addClicked());
		add(btnAddMapping, "2, 4");
		
		JButton btnRemoveMapping = new JButton("Remove Selected");
		btnRemoveMapping.addActionListener(e -> removeClicked());
		add(btnRemoveMapping, "4, 4");
		
		updateContents();
	}
	
	public final void updateContents()
	{
		Object[] columnNames = new Object[] { "Target type", "Column", "Data series" };
		
		displayedDescriptors = registrar.getService(DataSeriesMappingManager.class).getAllMappingDescriptors();
		Object[][] data = new Object[displayedDescriptors.size()][3];
		
		for(int row = 0; row < displayedDescriptors.size(); row++)
		{
			MappingDescriptor desc = displayedDescriptors.get(row); 
			data[row][0] = desc.getTargetClass().getSimpleName();
			data[row][1] = desc.getColumnName();
			data[row][2] = desc.getDataSeries().getName();
		}
		
		DefaultTableModel model = new DefaultTableModel(data, columnNames);
		table.setModel(model);
	}

	private void addClicked()
	{
		MapColumnTask task = new MapColumnTask(registrar.getService(DataSeriesManager.class), registrar.getService(DataSeriesMappingManager.class), registrar.getService(CyApplicationManager.class));
		startTask(task);
	}
	
	private void removeClicked()
	{
		RemoveColumnMappingTask task = new RemoveColumnMappingTask(registrar.getService(DataSeriesMappingManager.class));
		if(table.getSelectedRow() >= 0)
		{
			MappingDescriptor selectedDescriptor = displayedDescriptors.get(table.getSelectedRow());
			task.targetMapping.setSelectedValue(selectedDescriptor);
		}
		startTask(task);
	}
	
	private void startTask(Task task)
	{
		Task updateTask = new AbstractTask() {
			
			@Override
			public void run(TaskMonitor taskMonitor) throws Exception {
				updateContents();				
			}
		};
		TaskManager<?,?> mgr = registrar.getService(TaskManager.class);
		mgr.execute(new TaskIterator(task, updateTask));
	}
}
