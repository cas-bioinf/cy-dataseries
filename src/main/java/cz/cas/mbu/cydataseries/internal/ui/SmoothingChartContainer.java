package cz.cas.mbu.cydataseries.internal.ui;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;

public class SmoothingChartContainer {
	private final JFreeChart chart;
	private final DefaultXYDataset dataset;
	private final XYLineAndShapeRenderer renderer;
	
	private static final String NOISY_SERIES_KEY = "Noisy";
	private static final String SMOOTH_SERIES_KEY = "Smoothed";
	
	private final int noisySeriesIndex;
	private final int smoothSeriesIndex;
	
	public SmoothingChartContainer()
	{
		dataset = new DefaultXYDataset();
		dataset.addSeries(NOISY_SERIES_KEY, new double[][] {{0},{0}});
		dataset.addSeries(SMOOTH_SERIES_KEY, new double[][] {{0},{0}});

		noisySeriesIndex = 0;
		smoothSeriesIndex = 1;
		
		renderer = new XYLineAndShapeRenderer();
		XYPlot plot = new XYPlot(dataset, new NumberAxis("Time"), new NumberAxis(), renderer);
		chart = new JFreeChart(plot);
		plot.setDrawingSupplier(ChartUtils.createDrawingSupplier());
		
		renderer.setSeriesLinesVisible(noisySeriesIndex, false);
		renderer.setSeriesShapesVisible(noisySeriesIndex, true);
		
		renderer.setSeriesLinesVisible(smoothSeriesIndex, true);
		renderer.setSeriesShapesVisible(smoothSeriesIndex, false);
	}
	
	public JFreeChart getChart() {
		return chart;
	}
	
	public void setSmoothingData(double[] noisyX, double[] noisyY, double[] smoothedX, double[] smoothedY, String title)
	{
		while(dataset.getSeriesCount() > 0)
		{
			dataset.removeSeries(dataset.getSeriesKey(0));
		}
		chart.setTitle(title);
		dataset.addSeries(NOISY_SERIES_KEY, new double[][]{noisyX, noisyY});
		dataset.addSeries(SMOOTH_SERIES_KEY, new double[][]{smoothedX, smoothedY});
	}
	
}
