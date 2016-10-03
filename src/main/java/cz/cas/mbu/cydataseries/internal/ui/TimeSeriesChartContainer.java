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

import cz.cas.mbu.cydataseries.DataSeries;
import cz.cas.mbu.cydataseries.DataSeriesStorageProvider;
import cz.cas.mbu.cydataseries.MappingDescriptor;
import cz.cas.mbu.cydataseries.TimeSeries;

public class TimeSeriesChartContainer extends AbstractDataSeriesChartContainer {
	private final JFreeChart chart;
	private final DefaultXYDataset dataset;
	private final XYLineAndShapeRenderer renderer;
	
	
	public TimeSeriesChartContainer()
	{
		dataset = new DefaultXYDataset();
		renderer = new XYLineAndShapeRenderer();
		renderer.setBaseShapesVisible(false);
		renderer.setBaseLinesVisible(true);
		XYPlot plot = new XYPlot(dataset, new NumberAxis("Time"), new NumberAxis(), renderer);
		chart = new JFreeChart(plot);
		plot.setDrawingSupplier(ChartUtils.createDrawingSupplier());
		
	}
	
	public JFreeChart getChart()
	{
		return chart;
	}
	

	
	
	@Override
	protected void clearDataset() {
		while(dataset.getSeriesCount() > 0)
		{
			dataset.removeSeries(dataset.getSeriesKey(0));
		}
	}

	@Override
	protected int processSeriesRow(DataSeries<?, ?> seriesRaw, String label, int row) {
		if(seriesRaw instanceof TimeSeries)
		{
			TimeSeries series = (TimeSeries)seriesRaw;
			double [][] data = new double[][] { series.getIndexArray(), series.getRowDataArray(row) };
			dataset.addSeries(label, data);
			int index = dataset.getSeriesCount() - 1;
			return index;
		}
		else
		{
			return -1;			
		}
	}
	
	public void resetSeriesVisible()
	{
		for(int i = 0; i < dataset.getSeriesCount(); i++)
		{
			setSeriesVisible(i, true);			
		}
	}
	
	
	@Override
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
