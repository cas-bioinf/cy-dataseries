package cz.cas.mbu.cydataseries.internal;

import java.util.Collection;
import java.util.List;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;

import cz.cas.mbu.cydataseries.DataSeriesStorageProvider;
import cz.cas.mbu.cydataseries.TimeSeries;

public class TimeSeriesChartContainer {
	private JFreeChart chart;
	DefaultXYDataset dataset;
	XYLineAndShapeRenderer renderer;
	
	public TimeSeriesChartContainer()
	{
		dataset = new DefaultXYDataset();
		renderer = new XYLineAndShapeRenderer();
		renderer.setBaseShapesVisible(false);
		renderer.setBaseLinesVisible(true);
		XYPlot plot = new XYPlot(dataset, new NumberAxis("Time"), new NumberAxis(), renderer);
		chart = new JFreeChart(plot);		
	}
	
	public JFreeChart getChart()
	{
		return chart;
	}
	
	public void setSeriesData(List<TimeSeries> allSeries, List<Integer> rowIds)
	{
		while(dataset.getSeriesCount() > 0)
		{
			dataset.removeSeries(dataset.getSeriesKey(0));
		}
		
		for(int i = 0; i < allSeries.size(); i++)
		{
			TimeSeries series = allSeries.get(i);
			int row = series.idToRow(rowIds.get(i));
			if(row >= 0)
			{
				double [][] data = new double[][] { series.getIndexArray(), series.getRowDataArray(row) };
				dataset.addSeries(series.getRowName(row) + " (ID " + rowIds.get(i) + " in " + series.getName() + ")", data);
			}			
		}		
		
	}
	
	public void resetSeriesVisible()
	{
		for(int i = 0; i < dataset.getSeriesCount(); i++)
		{
			renderer.setSeriesLinesVisible(i, null);			
		}
	}
	
	public void setSeriesVisible(TimeSeries series, boolean visible)
	{
		int seriesIndex = dataset.indexOf(series.getName());
		renderer.setSeriesLinesVisible(seriesIndex, visible);
	}
	
}
