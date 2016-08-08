package cz.cas.mbu.cydataseries.internal.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;

import cz.cas.mbu.cydataseries.DataSeriesMappingManager;
import cz.cas.mbu.cydataseries.DataSeriesStorageProvider;
import cz.cas.mbu.cydataseries.TimeSeries;

public class TimeSeriesChartContainer {
	private JFreeChart chart;
	DefaultXYDataset dataset;
	XYLineAndShapeRenderer renderer;
	
	Map<DataSeriesMappingManager.MappingDescriptor, List<Integer>> descriptorsToIndex;
	
	public TimeSeriesChartContainer()
	{
		dataset = new DefaultXYDataset();
		renderer = new XYLineAndShapeRenderer();
		renderer.setBaseShapesVisible(false);
		renderer.setBaseLinesVisible(true);
		XYPlot plot = new XYPlot(dataset, new NumberAxis("Time"), new NumberAxis(), renderer);
		chart = new JFreeChart(plot);		
		
		descriptorsToIndex = new HashMap<>();
	}
	
	public JFreeChart getChart()
	{
		return chart;
	}
	
	public void setSeriesData(List<TimeSeries> allSeries,List<DataSeriesMappingManager.MappingDescriptor> descriptors, List<Integer> rowIds, List<Boolean> visible)
	{
		while(dataset.getSeriesCount() > 0)
		{
			dataset.removeSeries(dataset.getSeriesKey(0));
		}
		
		descriptorsToIndex.clear();
		
		for(int i = 0; i < allSeries.size(); i++)
		{
			TimeSeries series = allSeries.get(i);
			int row = series.idToRow(rowIds.get(i));
			if(row >= 0)
			{
				double [][] data = new double[][] { series.getIndexArray(), series.getRowDataArray(row) };
				dataset.addSeries(series.getRowName(row) + " (ID " + rowIds.get(i) + " in " + series.getName() + ")", data);
				
				if(!visible.get(i))
				{
					//The current series is the last series
					setSeriesVisible(dataset.getSeriesCount() - 1, false);
				}
				
				List<Integer> indexList = descriptorsToIndex.get(descriptors.get(i)); 
				if(indexList == null)
				{
					indexList = new ArrayList<>();					
					descriptorsToIndex.put(descriptors.get(i), indexList);					
				}
				indexList.add(dataset.getSeriesCount() - 1);
			}			
		}		
		
	}
	
	public void resetSeriesVisible()
	{
		for(int i = 0; i < dataset.getSeriesCount(); i++)
		{
			setSeriesVisible(i, true);			
		}
	}
	
	public void setSeriesVisible(DataSeriesMappingManager.MappingDescriptor descriptor, boolean visible)
	{
		List<Integer> seriesIndices = descriptorsToIndex.get(descriptor);
		if(seriesIndices != null)
		{
			seriesIndices.forEach(
					index -> setSeriesVisible(index.intValue(), visible)
					);
		}		
	}
	
	protected void setSeriesVisible(int seriesIndex, boolean visible)
	{
		Boolean visibleValue;
		if(visible)
		{
			visibleValue = null;			
		}
		else
		{
			visibleValue = false;
		}
		
		renderer.setSeriesLinesVisible(seriesIndex, visibleValue);
		renderer.setSeriesVisibleInLegend(seriesIndex,visibleValue);
		
	}
	
}
