package cz.cas.mbu.cydataseries.internal.ui;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import cz.cas.mbu.cydataseries.DataSeries;
import cz.cas.mbu.cydataseries.NamedDoubleDataSeries;

public class NamedDoubleSeriesChartContainer extends AbstractDataSeriesChartContainer {
	private final JFreeChart chart;
	private final DefaultCategoryDataset dataset;
	private final LineAndShapeRenderer renderer;
	
	
	public NamedDoubleSeriesChartContainer()
	{
		dataset = new DefaultCategoryDataset();
		renderer = new LineAndShapeRenderer();
		renderer.setBaseShapesVisible(false);
		renderer.setBaseLinesVisible(true);
		CategoryPlot plot = new CategoryPlot(dataset, new CategoryAxis(), new NumberAxis(), renderer);
		chart = new JFreeChart(plot);
		plot.setDrawingSupplier(ChartUtils.createDrawingSupplier());
		
	}
	
	@Override
	public JFreeChart getChart()
	{
		return chart;
	}
	
	
	
	@Override
	protected void clearDataset() {
		while(dataset.getRowCount() > 0)
		{
			dataset.removeRow(0);
		}
	}

	@Override
	protected int processSeriesRow(DataSeries<?, ?> seriesRaw, String label, int row) {
		if (seriesRaw instanceof NamedDoubleDataSeries)
		{
			NamedDoubleDataSeries series = (NamedDoubleDataSeries)seriesRaw;
			
			double[] rowData = series.getRowDataArray(row);
			for(int column = 0; column < series.getIndexCount();column++)
			{
				dataset.addValue(rowData[column], label, series.getIndex().get(column));
			}
			
			int index = dataset.getRowCount() - 1; 
			return index;
		}
		else
		{
			return -1;
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
