package cz.cas.mbu.cytimeseries.internal;

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

import cz.cas.mbu.cytimeseries.TimeSeriesMetadata;

public class TimeSeriesChartContainer<TYPE extends CyIdentifiable> {
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
	
	public void setSeriesData(Collection<TimeSeriesMetadata<TYPE>> allSeries, CyRow row)
	{
		//TODO this does not handle series deletion
		for(TimeSeriesMetadata<TYPE> series : allSeries)
		{
			List<Double> timePointsList = series.getTimePoints();
			double[] timePoints = new double[timePointsList.size()];
			for(int i = 0; i < timePointsList.size(); i++)
			{
				timePoints[i] = timePointsList.get(i);
			}
			double [][] data = new double[][] { timePoints, series.getData(row) };
			dataset.addSeries(series.getName(), data);				
		}		
	}
	
	public void resetSeriesVisible()
	{
		for(int i = 0; i < dataset.getSeriesCount(); i++)
		{
			renderer.setSeriesLinesVisible(i, null);			
		}
	}
	
	public void setSeriesVisible(TimeSeriesMetadata<TYPE> series, boolean visible)
	{
		int seriesIndex = dataset.indexOf(series.getName());
		renderer.setSeriesLinesVisible(seriesIndex, visible);
	}
	
}
