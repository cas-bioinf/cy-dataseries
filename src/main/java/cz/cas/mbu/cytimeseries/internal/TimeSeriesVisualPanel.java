package cz.cas.mbu.cytimeseries.internal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CytoPanelComponent2;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.model.events.RowsSetEvent;
import org.cytoscape.model.events.RowsSetListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.ArrayUtilities;

import cz.cas.mbu.cytimeseries.TimeSeriesMetadata;
import cz.cas.mbu.cytimeseries.DataSeriesManager;

public class TimeSeriesVisualPanel extends JPanel implements CytoPanelComponent2, RowsSetListener {

	private boolean ignoreSelection = false;
	
	//private final CyNetworkManager cyNetworkManager;
	private final CyApplicationManager cyApplicationManager;
	private final DataSeriesManager timeSeriesManager;
	
	private final TimeSeriesChartContainer<CyNode> nodeChart;
	private final ChartPanel nodeChartPanel;
	
	public TimeSeriesVisualPanel(CyApplicationManager cyApplicationManager, DataSeriesManager timeSeriesManager) {
		this.cyApplicationManager = cyApplicationManager;
		this.timeSeriesManager = timeSeriesManager;
		setLayout(new BorderLayout());
		
		nodeChart = new TimeSeriesChartContainer<>();
		nodeChartPanel = new ChartPanel(nodeChart.getChart(), getWidth(), getHeight(), ChartPanel.DEFAULT_MINIMUM_DRAW_WIDTH, ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT, Integer.MAX_VALUE, Integer.MAX_VALUE, ChartPanel.DEFAULT_BUFFER_USED, true /*properties*/, true /* save */, true /* print */, true /* zoom */, true /* tooltips */);

		
		this.add(nodeChartPanel, BorderLayout.CENTER);
		nodeChartPanel.addMouseListener(new MouseAdapter() {

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
		return "Time Series Visual";
	}

	@Override
	public Icon getIcon() {
		return null;
	}

	@Override
	public String getIdentifier() {
		return "cz.cas.mbu.gn.timeSeriesVisual";
	}

	private void updateCharts()
	{
		CyNetwork network = cyApplicationManager.getCurrentNetwork();
		List<CyNode> nodes = CyTableUtil.getNodesInState(network,"selected",true);
		if(!nodes.isEmpty())
		{
			CyNode node = nodes.get(0);
			nodeChart.setSeriesData(timeSeriesManager.getAllTimeSeries(network, CyNode.class), network.getRow(node));			
		}
	}
	
	@Override
	public void handleEvent(RowsSetEvent e) {
		if (!e.containsColumn(CyNetwork.SELECTED) || ignoreSelection)
			return;
	}

}
