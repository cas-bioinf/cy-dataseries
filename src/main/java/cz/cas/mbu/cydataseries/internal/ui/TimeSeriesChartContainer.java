package cz.cas.mbu.cydataseries.internal.ui;

import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.jfree.chart.ChartColor;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;

import cz.cas.mbu.cydataseries.DataSeriesStorageProvider;
import cz.cas.mbu.cydataseries.MappingDescriptor;
import cz.cas.mbu.cydataseries.TimeSeries;

public class TimeSeriesChartContainer {
	private JFreeChart chart;
	DefaultXYDataset dataset;
	XYLineAndShapeRenderer renderer;
	
	Map<MappingDescriptor<?>, List<Integer>> descriptorsToIndex;
	
	public TimeSeriesChartContainer()
	{
		dataset = new DefaultXYDataset();
		renderer = new XYLineAndShapeRenderer();
		renderer.setBaseShapesVisible(false);
		renderer.setBaseLinesVisible(true);
		XYPlot plot = new XYPlot(dataset, new NumberAxis("Time"), new NumberAxis(), renderer);
		chart = new JFreeChart(plot);
		plot.setDrawingSupplier(createDrawingSupplier());
		
		descriptorsToIndex = new HashMap<>();
	}
	
	public JFreeChart getChart()
	{
		return chart;
	}
	
	protected DrawingSupplier createDrawingSupplier()
	{
		//Our paint sequence (the default contains very light yellow, which is not legible
		Paint[] paintSequence = new Paint[] {
	            new Color(0xFF, 0x55, 0x55),
	            new Color(0x55, 0x55, 0xFF),
	            new Color(0x55, 0xFF, 0x55),
	            new Color(0xFF, 0x55, 0xFF),
	            new Color(0x55, 0xFF, 0xFF),
	            Color.pink,
	            Color.gray,
	            ChartColor.DARK_RED,
	            ChartColor.DARK_BLUE,
	            ChartColor.DARK_GREEN,
	            ChartColor.DARK_YELLOW,
	            ChartColor.DARK_MAGENTA,
	            ChartColor.DARK_CYAN,
	            Color.darkGray,
	            ChartColor.LIGHT_RED,
	            ChartColor.LIGHT_BLUE,
	            ChartColor.LIGHT_GREEN,
	            ChartColor.LIGHT_YELLOW,
	            ChartColor.LIGHT_MAGENTA,
	            ChartColor.LIGHT_CYAN,
	            Color.lightGray,
	            ChartColor.VERY_DARK_RED,
	            ChartColor.VERY_DARK_BLUE,
	            ChartColor.VERY_DARK_GREEN,
	            ChartColor.VERY_DARK_YELLOW,
	            ChartColor.VERY_DARK_MAGENTA,
	            ChartColor.VERY_DARK_CYAN,
	            ChartColor.VERY_LIGHT_RED,
	            ChartColor.VERY_LIGHT_BLUE,
	            ChartColor.VERY_LIGHT_GREEN,
	            ChartColor.VERY_LIGHT_YELLOW,
	            ChartColor.VERY_LIGHT_MAGENTA,
	            ChartColor.VERY_LIGHT_CYAN
	        };		
		
		//Replace the drawing supplier with a supplier with all defaults, but a different paint sequence
		return new DefaultDrawingSupplier(paintSequence,
				DefaultDrawingSupplier.DEFAULT_FILL_PAINT_SEQUENCE, 
				DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE, 
				DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
				DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
				DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE);

		
	}		
	
	public void setSeriesData(List<TimeSeries> allSeries,List<MappingDescriptor<?>> descriptors, List<Integer> rowIds, List<Boolean> visible)
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
	
	public void setSeriesVisible(MappingDescriptor<?> descriptor, boolean visible)
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
