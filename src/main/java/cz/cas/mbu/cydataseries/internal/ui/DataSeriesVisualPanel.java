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

public class DataSeriesVisualPanel extends JPanel implements CytoPanelComponent2, RowsSetListener {

	private boolean ignoreSelection = false;
	
	//private final CyNetworkManager cyNetworkManager;
	private final CyApplicationManager cyApplicationManager;
	private final DataSeriesManager dataSeriesManager;
	private final DataSeriesMappingManager dataSeriesMappingManager;
	
	private final TimeSeriesChartContainer chartContainer;
	private final ChartPanel chartPanel;
	
	public DataSeriesVisualPanel(CyApplicationManager cyApplicationManager, DataSeriesManager dataSeriesManager, DataSeriesMappingManager dataSeriesMappingManager) {
		this.cyApplicationManager = cyApplicationManager;
		this.dataSeriesManager = dataSeriesManager;
		this.dataSeriesMappingManager = dataSeriesMappingManager;
		setLayout(new BorderLayout());
		
		chartContainer = new TimeSeriesChartContainer();
		chartPanel = new ChartPanel(chartContainer.getChart(), getWidth(), getHeight(), ChartPanel.DEFAULT_MINIMUM_DRAW_WIDTH, ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT, Integer.MAX_VALUE, Integer.MAX_VALUE, ChartPanel.DEFAULT_BUFFER_USED, true /*properties*/, true /* save */, true /* print */, true /* zoom */, true /* tooltips */);

		
		this.add(chartPanel, BorderLayout.CENTER);
		chartPanel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				updateCharts();
			}
			
		});
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
	
	private void updateChartsWithRow(Class<? extends CyIdentifiable> targetClass, CyRow row)
	{
		List<TimeSeries> allSeries = new ArrayList<>();
		List<Integer> rowIds = new ArrayList<>();
		
		dataSeriesMappingManager.getAllMappings(targetClass, TimeSeries.class).entrySet().forEach(
				(entry) -> {
					Integer id = row.get(entry.getKey(), DataSeriesMappingManager.MAPPING_COLUMN_CLASS);
					if(id != null)
					{
						allSeries.add(entry.getValue());
						rowIds.add(id);							
					}
				});
		
		chartContainer.setSeriesData(allSeries, rowIds);			
		
	}

	private void updateCharts()
	{
		CyNetwork network = cyApplicationManager.getCurrentNetwork();
		List<CyNode> nodes = CyTableUtil.getNodesInState(network,"selected",true);
		if(!nodes.isEmpty())
		{
			CyNode node = nodes.get(0);
			CyRow row = network.getRow(node);
			updateChartsWithRow(CyNode.class, row);			
		}
		else
		{
			List<CyEdge> edges = CyTableUtil.getEdgesInState(network, "selected", true);
			if(!edges.isEmpty())
			{
				CyRow row = network.getRow(edges.get(0));
				updateChartsWithRow(CyEdge.class, row);				
			}
			else
			{
				chartContainer.setSeriesData(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
			}
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

}
