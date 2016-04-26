package cz.cas.mbu.cytimeseries.internal;

import java.util.List;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;

import cz.cas.mbu.cytimeseries.TimeSeries;
import cz.cas.mbu.cytimeseries.TimeSeriesException;

public class TimeSeriesImpl<TARGET_CLASS extends CyIdentifiable> implements TimeSeries<TARGET_CLASS>  {
	private final CyRow row;
	private final Class<TARGET_CLASS> targetClass;
	
	public TimeSeriesImpl(CyRow row, Class<TARGET_CLASS> targetClass) {
		super();
		this.row = row;
		this.targetClass = targetClass;
	}

	@Override
	public Long getSUID() {
		return row.get(SUID, Long.class);
	}	

	@Override
	public Class<TARGET_CLASS> getTargetClass() {
		return targetClass;
	}

	@Override
	public String getName() {
		return row.get(NAME_ATTRIBUTE, String.class);
	}

	@Override
	public void setName(String name) {
		row.set(NAME_ATTRIBUTE, name);
	}

	@Override
	public TimeSeriesSourceType getSourceType() {
		return TimeSeriesSourceType.valueOf(row.get(SOURCE_TYPE_ATTRIBUTE, String.class));
	}

	@Override
	public void setSourceType(TimeSeriesSourceType sourceType) {
		row.set(SOURCE_TYPE_ATTRIBUTE, sourceType.name());
	}

	@Override
	public List<String> getDataColumns() {
		return row.getList(DATA_COLUMNS_ATTRIBUTE, String.class);
	}

	@Override
	public List<Double> getTimePoints() {
		return row.getList(TIME_POINTS_ATTRIBUTE, Double.class);
	}
	
	@Override
	public CyRow getRow()
	{
		return row;
	}

	@Override
	public void setTimePoints(List<Double> timePoints) {
		row.set(TIME_POINTS_ATTRIBUTE, timePoints);		
	}

	@Override
	public void setDataColumns(List<String> dataColumns) {
		row.set(DATA_COLUMNS_ATTRIBUTE, dataColumns);
	}

	@Override
	public double[] getData(CyRow row) {
		double result[] = new double[getTimePoints().size()];
		List<String> dataColumns = getDataColumns();
		for(int i = 0; i < getDataColumns().size(); i++)
		{
			Double value = row.get(dataColumns.get(i), Double.class, null);
			if(value == null)
			{
				throw new TimeSeriesException("Column '" + dataColumns.get(i) + "' required for time series '" + getName() + "' is not present");
			}
			result[i] = value;
		}
		return result;
	}
	
	
}
