package cz.cas.mbu.cydataseries.internal.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jfree.chart.JFreeChart;

import cz.cas.mbu.cydataseries.DataSeries;
import cz.cas.mbu.cydataseries.MappingDescriptor;

public abstract class AbstractDataSeriesChartContainer {
	
	Map<MappingDescriptor<?>, List<Integer>> descriptorsToIndex;
	
	Set<Integer> visibleSeriesIndices;
	
	public AbstractDataSeriesChartContainer()
	{		
		descriptorsToIndex = new HashMap<>();
		visibleSeriesIndices = new HashSet<>();
	}
	
	public abstract JFreeChart getChart();
	
	public boolean hasSeriesToShow()
	{
		return !visibleSeriesIndices.isEmpty();
	}
	
	
	protected abstract void clearDataset();
	
	protected abstract void setSeriesVisible(int seriesIndex, boolean visible);
	
	/**
	 * 
	 * @param series
	 * @param label
	 * @param row
	 * @return index of the data corresponding the the processed row (to be used with {@link #setSeriesVisible(int, boolean)}. -1 if the data series cannot be processed by this container
	 */
	protected abstract int processSeriesRow(DataSeries<?, ?> series, String label, int row);  

	public void setSeriesData(List<DataSeries<?,?>> allSeries,List<MappingDescriptor<?>> descriptors, List<Integer> rowIds, List<Boolean> visible)
	{
		clearDataset();
		
		descriptorsToIndex.clear();
		visibleSeriesIndices.clear();
		
		for(int i = 0; i < allSeries.size(); i++)
		{
			DataSeries<?,?> series = allSeries.get(i);
			int row = series.idToRow(rowIds.get(i));
			if(row >= 0)
			{
				String label = series.getRowName(row) + " (ID " + rowIds.get(i) + " in " + series.getName() + ")";
				int index = processSeriesRow(series, label, row);
				if(index >= 0)
				{
					setSeriesVisible(index, visible.get(i));
					
					if(visible.get(i))
					{
						visibleSeriesIndices.add(index);
					}
					
					List<Integer> indexList = descriptorsToIndex.get(descriptors.get(i)); 
					if(indexList == null)
					{
						indexList = new ArrayList<>();					
						descriptorsToIndex.put(descriptors.get(i), indexList);					
					}
					indexList.add(index);
					
					
				}
				
			}			
		}		
		
	}
		
	public void setSeriesVisible(MappingDescriptor<?> descriptor, boolean visible)
	{
		List<Integer> seriesIndices = descriptorsToIndex.get(descriptor);
		if(seriesIndices != null)
		{
			seriesIndices.forEach(
					index -> {
						setSeriesVisible(index.intValue(), visible);
						if(visible)
						{
							visibleSeriesIndices.add(index);							
						}
						else
						{
							visibleSeriesIndices.remove(index);
						}
					});
		}		
	}
		
}
