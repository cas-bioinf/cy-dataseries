package cz.cas.mbu.cydataseries.internal.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.DrawingSupplier;
import org.omg.CORBA.NamedValue;

import cz.cas.mbu.cydataseries.DataSeries;
import cz.cas.mbu.cydataseries.DataSeriesManager;
import cz.cas.mbu.cydataseries.DataSeriesMappingManager;
import cz.cas.mbu.cydataseries.MappingDescriptor;
import cz.cas.mbu.cydataseries.TimeSeries;

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

	List<AbstractDataSeriesChartContainer> chartContainers;
	List<ChartPanel> chartPanels;
	private JCheckBox showAdjacentCheckbox;
	private JPanel filteringPanel;
	private final JPanel chartContainerPanel;
	
	private final Set<MappingDescriptor<?>> hiddenSeries;
	private List<MappingDescriptor<?>> displayedDataSeries;  
	private JLabel lblSeriesToDisplay;
	
	public DataSeriesVisualPanel(CyApplicationManager cyApplicationManager, DataSeriesManager dataSeriesManager, DataSeriesMappingManager dataSeriesMappingManager) {
		this.cyApplicationManager = cyApplicationManager;
		this.dataSeriesManager = dataSeriesManager;
		this.dataSeriesMappingManager = dataSeriesMappingManager;
		setLayout(new BorderLayout());
		
		hiddenSeries = new HashSet<>();
		displayedDataSeries = new ArrayList<>();
		
		chartContainers = new ArrayList<>();
		chartContainers.add(new TimeSeriesChartContainer());
		chartContainers.add(new NamedDoubleSeriesChartContainer());
		
		chartPanels = new ArrayList<>();
		for(AbstractDataSeriesChartContainer chartContainer: chartContainers)
		{
			ChartPanel newPanel = new ChartPanel(chartContainer.getChart());
			chartPanels.add(newPanel);
		}
		
		
		chartContainerPanel = new JPanel(new GridLayout(1, chartContainers.size()));
				
		this.add(chartContainerPanel, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.EAST);
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
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
		
		lblSeriesToDisplay = new JLabel("Series to display:");
		panel.add(lblSeriesToDisplay, "2, 4");
		
		filteringPanel = new JPanel();
		panel.add(filteringPanel, "2, 6, fill, fill");
		filteringPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		showAdjacentCheckbox.addItemListener(e -> updateVisual());
		
		updateVisual();
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
	
	private void updateVisualWithRows(List<ChartSource> rowSources)
	{
		List<DataSeries<?, ?>> allSeries = new ArrayList<>();		
		List<Integer> rowIds = new ArrayList<>();
		List<Boolean> seriesVisible = new ArrayList<>();
		displayedDataSeries.clear();
		
		rowSources.forEach(
				source -> {
					dataSeriesMappingManager.getAllMappings(source.getNetwork(), source.getTargetClass()).entrySet()
						.forEach((entry) -> {
							String columnName = entry.getKey();
							DataSeries<?,?> ds = entry.getValue();
							
							Integer id = source.getRow().get(columnName, DataSeriesMappingManager.MAPPING_COLUMN_CLASS);
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
									allSeries.add(ds);
									rowIds.add(id);
									
									MappingDescriptor<DataSeries<?,?>> descriptor = new MappingDescriptor<DataSeries<?,?>>(source.getNetwork(), source.getTargetClass(), columnName, ds);
									displayedDataSeries.add(descriptor);
									
									seriesVisible.add( !hiddenSeries.contains(descriptor) );
									
								}
							}
						});
				});
				
		for(AbstractDataSeriesChartContainer chartContainer: chartContainers)
		{
			chartContainer.setSeriesData(allSeries, displayedDataSeries, rowIds, seriesVisible);						
		}
		updateChartVisibility();
		updateFilteringPanel(rowSources);		
	}

	private void updateChartVisibility()
	{
		chartContainerPanel.removeAll();
		boolean showedAnyCharts = false;
		for(int chartIndex = 0; chartIndex < chartPanels.size(); chartIndex++)
		{
			if(chartContainers.get(chartIndex).hasSeriesToShow())
			{
				chartContainerPanel.add(chartPanels.get(chartIndex));
				showedAnyCharts = true;
			}
		}
		
		if(!showedAnyCharts)
		{
			chartContainerPanel.add(new JLabel("No data series to display."));
		}
		
		chartContainerPanel.revalidate();
		chartContainerPanel.repaint();
		
	}
	
	private void updateVisual()
	{
		final CyNetwork network = cyApplicationManager.getCurrentNetwork();
		if(network == null)
		{
			updateVisualWithRows(Collections.EMPTY_LIST);
		}
		else
		{
			List<CyNode> nodes = CyTableUtil.getNodesInState(network,"selected",true);
			List<ChartSource> sources = new ArrayList<>();
						
			
			nodes.forEach(node -> {
				CyRow row = network.getRow(node);
				sources.add(new ChartSource(network, CyNode.class, row));
				
				if(getShowAdjacentCheckbox().isSelected())
				{
					network.getAdjacentEdgeList(node, CyEdge.Type.ANY).forEach( edge ->
					{
						sources.add(new ChartSource(network, CyEdge.class, network.getRow(edge)));
					});
				}			
			});
			
			List<CyEdge> edges = CyTableUtil.getEdgesInState(network, "selected", true);
			edges.forEach(edge -> {
				CyRow row = network.getRow(edge);
				sources.add(new ChartSource(network, CyEdge.class, row));
				
				if(getShowAdjacentCheckbox().isSelected())
				{
					sources.add(new ChartSource(network, CyNode.class, network.getRow(edge.getSource())));
					sources.add(new ChartSource(network, CyNode.class, network.getRow(edge.getTarget())));
				}			
			});
	
			updateVisualWithRows(sources);
		}
	}
	
	private void updateFilteringPanel(List<ChartSource> rowSources)
	{
		filteringPanel.removeAll();
				
		if(rowSources.isEmpty())
		{
			filteringPanel.add(new JLabel("Nothing selected"));			
		}
		else if(displayedDataSeries.isEmpty())
		{
			filteringPanel.add(new JLabel("No series available."));
		}
		else
		{
			Set<MappingDescriptor<?>> displayedDescriptors = new HashSet<>();
			for(int i = 0; i < displayedDataSeries.size(); i++)
			{
				final MappingDescriptor<?> descriptor = displayedDataSeries.get(i);
				if(displayedDescriptors.contains(descriptor))
				{
					continue;
				}
				
				displayedDescriptors.add(descriptor);
				JCheckBox seriesCheckBox = new JCheckBox(descriptor.getDataSeries().getName() + " (" + descriptor.getColumnName() + ")");
				if(!hiddenSeries.contains(descriptor))
				{
					seriesCheckBox.setSelected(true);				
				}
				seriesCheckBox.addItemListener(e -> {
					filteringItemChanged(descriptor, e.getStateChange() == ItemEvent.SELECTED);
				});
				filteringPanel.add(seriesCheckBox);			
			}
		}
		
		filteringPanel.revalidate();
		filteringPanel.repaint();
	}
	
	private void filteringItemChanged(MappingDescriptor<?> descriptor, boolean selected)
	{
		if(selected)
		{
			hiddenSeries.remove(descriptor);
		}
		else
		{
			hiddenSeries.add(descriptor);
		}
		
		for(AbstractDataSeriesChartContainer chartContainer: chartContainers)
		{
			chartContainer.setSeriesVisible(descriptor, selected);			
		}
		
		updateChartVisibility();
			
	}

	@Override
	public void handleEvent(RowsSetEvent e) {
		if (!e.containsColumn(CyNetwork.SELECTED) || ignoreSelection) 
		{
			return;
		}
		
		CyNetwork currentNetwork = cyApplicationManager.getCurrentNetwork();
		
		if(currentNetwork == null 
				|| e.getSource().equals(currentNetwork.getTable(CyNode.class, CyNetwork.LOCAL_ATTRS)) 
				|| e.getSource().equals(currentNetwork.getTable(CyEdge.class, CyNetwork.LOCAL_ATTRS)))
		{
			updateVisual();
	
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
		private final CyNetwork network;
		private final Class<? extends CyIdentifiable> targetClass;
		private final CyRow row;
		
		public ChartSource(CyNetwork network, Class<? extends CyIdentifiable> targetClass, CyRow row) {
			super();
			this.network = network;
			this.targetClass = targetClass;
			this.row = row;
		}

		public CyNetwork getNetwork(){
			return network;
		}
		
		public Class<? extends CyIdentifiable> getTargetClass() {
			return targetClass;
		}
		
		public CyRow getRow() {
			return row;
		}
		
		
		
	}
}
