package cz.cas.mbu.cydataseries.internal.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JPanel;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CytoPanelComponent2;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.model.events.RowsSetEvent;
import org.cytoscape.model.events.RowsSetListener;
import org.jfree.chart.ChartPanel;

import cz.cas.mbu.cydataseries.DataSeriesManager;
import cz.cas.mbu.cydataseries.DataSeriesMappingManager;
import cz.cas.mbu.cydataseries.TimeSeries;
import cz.cas.mbu.cydataseries.internal.TimeSeriesChartContainer;
import javax.swing.JCheckBox;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JLabel;
import javax.swing.JSeparator;

public class DataSeriesVisualPanel extends JPanel implements CytoPanelComponent2, RowsSetListener {

	private boolean ignoreSelection = false;
	
	//private final CyNetworkManager cyNetworkManager;
	private final CyApplicationManager cyApplicationManager;
	private final DataSeriesManager dataSeriesManager;
	private final DataSeriesMappingManager dataSeriesMappingManager;
	
	private final TimeSeriesChartContainer chartContainer;
	private final ChartPanel chartPanel;
	private JCheckBox showAdjacentCheckbox;
	
	public DataSeriesVisualPanel(CyApplicationManager cyApplicationManager, DataSeriesManager dataSeriesManager, DataSeriesMappingManager dataSeriesMappingManager) {
		this.cyApplicationManager = cyApplicationManager;
		this.dataSeriesManager = dataSeriesManager;
		this.dataSeriesMappingManager = dataSeriesMappingManager;
		setLayout(new BorderLayout());
		
		chartContainer = new TimeSeriesChartContainer();
		chartPanel = new ChartPanel(chartContainer.getChart(), getWidth(), getHeight(), ChartPanel.DEFAULT_MINIMUM_DRAW_WIDTH, ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT, Integer.MAX_VALUE, Integer.MAX_VALUE, ChartPanel.DEFAULT_BUFFER_USED, true /*properties*/, true /* save */, true /* print */, true /* zoom */, true /* tooltips */);

		
		this.add(chartPanel, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.EAST);
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		showAdjacentCheckbox = new JCheckBox("Show series from neighbourhood");
		panel.add(showAdjacentCheckbox, "2, 2");
		showAdjacentCheckbox.setSelected(true);
		
		JSeparator separator = new JSeparator();
		panel.add(separator, "1, 3, 3, 1");
		
		showAdjacentCheckbox.addItemListener(e -> updateCharts());
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
		return "Data Series Visual";
	}

	@Override
	public Icon getIcon() {
		return null;
	}

	@Override
	public String getIdentifier() {
		return "cz.cas.mbu.cydataseries.dataSeriesVisual";
	}
	
	private void updateChartsWithRows(List<ChartSource> rowSources)
	{
		List<TimeSeries> allSeries = new ArrayList<>();
		List<Integer> rowIds = new ArrayList<>();

		rowSources.forEach(
				source -> {
					dataSeriesMappingManager.getAllMappings(source.getTargetClass(), TimeSeries.class).entrySet()
						.forEach((entry) -> {
							Integer id = source.getRow().get(entry.getKey(), DataSeriesMappingManager.MAPPING_COLUMN_CLASS);
							if(id != null)
							{
								//Ignore duplicate series+id pairs
								boolean alreadyPresent = false;
								for(int idx = 0; idx < allSeries.size();idx++)
								{
									if(allSeries.get(idx) == entry.getValue() && rowIds.get(idx).equals(id))
									{
										alreadyPresent = true;
										break;
									}
								}
								if(!alreadyPresent)
								{
									allSeries.add(entry.getValue());
									rowIds.add(id);
								}
							}
						});
				});
		
		chartContainer.setSeriesData(allSeries, rowIds);			
		
	}

	private void updateCharts()
	{
		CyNetwork network = cyApplicationManager.getCurrentNetwork();
		List<CyNode> nodes = CyTableUtil.getNodesInState(network,"selected",true);
		List<ChartSource> sources = new ArrayList<>();
		
		
		
		nodes.forEach(node -> {
			CyRow row = network.getRow(node);
			sources.add(new ChartSource(CyNode.class, row));
			
			if(getShowAdjacentCheckbox().isSelected())
			{
				network.getAdjacentEdgeList(node, CyEdge.Type.ANY).forEach( edge ->
				{
					sources.add(new ChartSource(CyEdge.class, network.getRow(edge)));
				});
			}			
		});
		
		List<CyEdge> edges = CyTableUtil.getEdgesInState(network, "selected", true);
		edges.forEach(edge -> {
			CyRow row = network.getRow(edge);
			sources.add(new ChartSource(CyEdge.class, row));
			
			if(getShowAdjacentCheckbox().isSelected())
			{
				sources.add(new ChartSource(CyNode.class, network.getRow(edge.getSource())));
				sources.add(new ChartSource(CyNode.class, network.getRow(edge.getTarget())));
			}			
		});
		if(!sources.isEmpty())
		{
			updateChartsWithRows(sources);			
		}
		else
		{
			chartContainer.setSeriesData(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
		}
	}
	
	@Override
	public void handleEvent(RowsSetEvent e) {
		if (!e.containsColumn(CyNetwork.SELECTED) || ignoreSelection)
			return;
		if(e.getSource().equals(cyApplicationManager.getCurrentNetwork().getTable(CyNode.class, CyNetwork.LOCAL_ATTRS)) 
				|| e.getSource().equals(cyApplicationManager.getCurrentNetwork().getTable(CyEdge.class, CyNetwork.LOCAL_ATTRS)))
		{
			updateCharts();
	
			//TODO the above is very inefficient, replace with maintaining the list of selected nodes with
//			 for (RowSetRecord record: e.getColumnRecords(CyNetwork.SELECTED)) {
//		          Long suid = record.getRow().get(CyIdentifiable.SUID, Long.class);
//		          Boolean value = (Boolean)record.getValue();
			
		}
	}

	protected JCheckBox getShowAdjacentCheckbox() {
		return showAdjacentCheckbox;
	}
	
	private static class ChartSource
	{
		private final Class<? extends CyIdentifiable> targetClass;
		private final CyRow row;
		
		public ChartSource(Class<? extends CyIdentifiable> targetClass, CyRow row) {
			super();
			this.targetClass = targetClass;
			this.row = row;
		}
		
		public Class<? extends CyIdentifiable> getTargetClass() {
			return targetClass;
		}
		
		public CyRow getRow() {
			return row;
		}
		
		
	}
}
